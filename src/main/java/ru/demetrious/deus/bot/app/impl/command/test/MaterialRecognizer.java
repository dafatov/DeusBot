package ru.demetrious.deus.bot.app.impl.command.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import ru.demetrious.deus.bot.domain.reverse1999.ReverseData;

import static java.util.stream.Collectors.toMap;
import static ru.demetrious.deus.bot.app.impl.command.Reverse1999MaterialsSetCommandUseCase.toMat;

@Slf4j
public class MaterialRecognizer {
    private static final double MATCH_THRESHOLD = 500.0;

    @NotNull
    private final Map<Integer, Mat> templateHashes;
    private final List<Slot> allSlots = new ArrayList<>();

    public MaterialRecognizer(ReverseData reverseData) {
        this.templateHashes = reverseData.getItems().entrySet().stream()
            .collect(toMap(Map.Entry::getKey, entry -> prepare(toMat(entry.getValue().getImage().toBufferedImage()))));

    }

    public int collectSlot(Pair<Rect, Mat> slotMat, int slotId) {
        Mat prepared = prepare(slotMat.getValue());

        allSlots.add(new Slot(slotMat.getKey(), slotMat.getValue(), slotId, prepared));
        return slotId;
    }

    public List<GlobalRecognitionResult> performGlobalRecognition() {
        if (allSlots.isEmpty()) {
            return Collections.emptyList();
        }

        // Собираем все возможные соответствия
        List<MatchCandidate> allCandidates = new ArrayList<>();
        List<Integer> templateIds = new ArrayList<>(templateHashes.keySet());

        // Заполняем список кандидатов
        for (Slot slot : allSlots) {
            Mat prepared = slot.prepared;

            for (Integer templateId : templateIds) {
                Mat template = templateHashes.get(templateId);

                double akazeScore = compareWithAKAZE(prepared, template);
                double orbScore = compareWithORB(prepared, template);
                double combinedScore = (akazeScore + orbScore) / 2;

                double histDist = compareHistograms(prepared, template);
                combinedScore = (2 * combinedScore + combinedScore * Math.pow(histDist, 2)) / 3; // Опционально: добавьте взвешенный штраф за мелкие различия

                // Добавляем только если combinedScore разумный (избегаем бесполезных кандидатов)
                if (combinedScore < Double.MAX_VALUE / 2) {
                    allCandidates.add(new MatchCandidate(
                        slot.slotId, templateId, combinedScore, akazeScore, orbScore
                    ));
                }
            }
        }

        // Сортируем кандидатов по качеству совпадения (лучшие сначала, низкий score - лучше)
        allCandidates.sort(Comparator.comparingDouble(c -> c.combinedScore));

        // Алгоритм жадного назначения с проверкой уникальности
        Map<Integer, Integer> slotToTemplate = new HashMap<>();
        Map<Integer, Integer> templateToSlot = new HashMap<>();
        Map<Integer, Double> matchQuality = new HashMap<>();

        for (MatchCandidate candidate : allCandidates) {
            // Если слот еще не назначен, шаблон еще не использован, и скор достаточно хороший
            if (!slotToTemplate.containsKey(candidate.slotId) &&
                !templateToSlot.containsKey(candidate.templateId) &&
                candidate.combinedScore < MATCH_THRESHOLD) {

                slotToTemplate.put(candidate.slotId, candidate.templateId);
                templateToSlot.put(candidate.templateId, candidate.slotId);
                matchQuality.put(candidate.slotId, candidate.combinedScore);
            }
        }

        // Формируем результаты
        List<GlobalRecognitionResult> results = new ArrayList<>();

        for (Slot slot : allSlots) {
            if (slotToTemplate.containsKey(slot.slotId)) {
                int templateId = slotToTemplate.get(slot.slotId);
                double score = matchQuality.get(slot.slotId);

                results.add(new GlobalRecognitionResult(
                    slot.slotId, templateId, slot.rect, score
                ));
            } else {
                // Нераспознанный слот
                results.add(new GlobalRecognitionResult(
                    slot.slotId, -1, slot.rect, Double.MAX_VALUE
                ));
            }
        }

        // Дополнительная оптимизация: попробуем улучшить назначения
        // путем попарных обменов, если это улучшает общее качество
        optimizeBySwapping(slotToTemplate, templateToSlot, matchQuality, allCandidates);

        // Обновляем результаты после оптимизации
        results.clear();
        for (Slot slot : allSlots) {
            if (slotToTemplate.containsKey(slot.slotId)) {
                int templateId = slotToTemplate.get(slot.slotId);
                double score = matchQuality.getOrDefault(slot.slotId, Double.MAX_VALUE);

                results.add(new GlobalRecognitionResult(
                    slot.slotId, templateId, slot.rect, score
                ));
            } else {
                results.add(new GlobalRecognitionResult(
                    slot.slotId, -1, slot.rect, Double.MAX_VALUE
                ));
            }
        }

        return results;
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    public Mat prepare(Mat src) {

        Mat img = src.clone();

        // --- 1. Создаем маску ---
        Mat mask = new Mat(img.size(), CvType.CV_8UC1, new Scalar(Imgproc.GC_PR_BGD));

        // --- 2. Прямоугольник по центру изображения ---
        int w = img.width();
        int h = img.height();
        int rectW = (int) (w * 0.8);
        int rectH = (int) (h * 0.8);
        int rectX = (w - rectW) / 2;
        int rectY = (h - rectH) / 2;

        Rect rect = new Rect(rectX, rectY, rectW, rectH);

        // --- 3. Модели фона/объекта ---
        Mat bgdModel = new Mat();
        Mat fgdModel = new Mat();

        // --- 4. Запуск GrabCut ---
        Imgproc.grabCut(
            img,
            mask,
            rect,
            bgdModel,
            fgdModel,
            5,
            Imgproc.GC_INIT_WITH_RECT
        );

        // --- 5. Создаём бинарную маску объекта ---
        Mat foregroundMask = new Mat(mask.size(), CvType.CV_8UC1);
        Core.compare(mask, new Scalar(Imgproc.GC_PR_FGD), foregroundMask, Core.CMP_EQ);

        // Также добавляем явно принадлежность объекту
        Mat sureFg = new Mat();
        Core.compare(mask, new Scalar(Imgproc.GC_FGD), sureFg, Core.CMP_EQ);
        Core.bitwise_or(foregroundMask, sureFg, foregroundMask);

        // --- 6. Морфология: чуть расширим объект, чтобы убрать "ореол" ---
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(foregroundMask, foregroundMask, Imgproc.MORPH_CLOSE, kernel);

        // --- 7. Применяем маску к изображению ---
        Mat result = new Mat(img.size(), CvType.CV_8UC4);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2BGRA);

        for (int y = 0; y < img.rows(); y++) {
            for (int x = 0; x < img.cols(); x++) {
                double f = foregroundMask.get(y, x)[0]; // 0 или 255
                double[] pixel = img.get(y, x);
                pixel[3] = f; // альфа-канал
                result.put(y, x, pixel);
            }
        }


        int borderMargin = (int) (result.height() * 0.05);
        int textPanelHeight = (int) (result.height() * 0.25);
        Rect roi1 = new Rect(borderMargin, borderMargin, result.width() - 2 * borderMargin, result.height() - borderMargin - textPanelHeight);

        return new Mat(result, roi1);
    }

    private double compareWithAKAZE(Mat img1, Mat img2) {
        AKAZE akaze = AKAZE.create();
        return extracted(img1, img2, akaze);
    }

    private double compareWithORB(Mat img1, Mat img2) {
        ORB orb = ORB.create();
        return extracted(img1, img2, orb);
    }

    private double compareHistograms(Mat img1, Mat img2) {
        Mat hsv1 = new Mat();
        Mat hsv2 = new Mat();
        Imgproc.cvtColor(img1, hsv1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(img2, hsv2, Imgproc.COLOR_BGR2HSV);

        Mat mask1 = new Mat();
        Mat mask2 = new Mat();
        Core.extractChannel(img1, mask1, 3); // Альфа-канал
        Core.extractChannel(img2, mask2, 3);
        Core.compare(mask1, new Scalar(0), mask1, Core.CMP_GT); // Маска, где альфа > 0
        Core.compare(mask2, new Scalar(0), mask2, Core.CMP_GT);

        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
        List<Mat> hsv1List = Collections.singletonList(hsv1);
        List<Mat> hsv2List = Collections.singletonList(hsv2);

        Imgproc.calcHist(hsv1List, new MatOfInt(0, 1), mask1, hist1, new MatOfInt(32, 32), new MatOfFloat(0f, 180f, 0f, 256f));
        Imgproc.calcHist(hsv2List, new MatOfInt(0, 1), mask2, hist2, new MatOfInt(32, 32), new MatOfFloat(0f, 180f, 0f, 256f));

        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);

        return Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_BHATTACHARYYA); // 0 = идентичны, 1 = полностью разные
    }

    /**
     * Оптимизация путем попарных обменов
     */
    private void optimizeBySwapping(Map<Integer, Integer> slotToTemplate,
                                    Map<Integer, Integer> templateToSlot,
                                    Map<Integer, Double> matchQuality,
                                    List<MatchCandidate> allCandidates) {

        boolean improved;
        int maxIterations = 10;

        do {
            improved = false;

            // Получаем список назначенных слотов
            List<Integer> assignedSlots = new ArrayList<>(slotToTemplate.keySet());

            for (int i = 0; i < assignedSlots.size(); i++) {
                for (int j = i + 1; j < assignedSlots.size(); j++) {
                    int slotA = assignedSlots.get(i);
                    int slotB = assignedSlots.get(j);
                    int templateA = slotToTemplate.get(slotA);
                    int templateB = slotToTemplate.get(slotB);

                    // Текущее качество
                    double currentQuality = matchQuality.get(slotA) + matchQuality.get(slotB);

                    // Находим скоры для возможного обмена
                    double scoreAtoB = findScore(allCandidates, slotA, templateB);
                    double scoreBtoA = findScore(allCandidates, slotB, templateA);

                    // Если обмен улучшает суммарное качество и скоры валидны
                    if (scoreAtoB < MATCH_THRESHOLD &&
                        scoreBtoA < MATCH_THRESHOLD &&
                        (scoreAtoB + scoreBtoA) < currentQuality) {

                        // Выполняем обмен
                        slotToTemplate.put(slotA, templateB);
                        slotToTemplate.put(slotB, templateA);
                        templateToSlot.put(templateA, slotB);
                        templateToSlot.put(templateB, slotA);
                        matchQuality.put(slotA, scoreAtoB);
                        matchQuality.put(slotB, scoreBtoA);

                        improved = true;
                    }
                }
            }

            maxIterations--;
        } while (improved && maxIterations > 0);
    }

    public double extracted(Mat img1, Mat img2, Feature2D detector) {
        MatOfKeyPoint k1 = new MatOfKeyPoint(), k2 = new MatOfKeyPoint();
        Mat d1 = new Mat(), d2 = new Mat();

        Mat gray1 = new Mat();
        Imgproc.cvtColor(img1, gray1, Imgproc.COLOR_BGRA2GRAY);
        Mat gray2 = new Mat();
        Imgproc.cvtColor(img2, gray2, Imgproc.COLOR_BGRA2GRAY);

        detector.detectAndCompute(gray1, new Mat(), k1, d1);
        detector.detectAndCompute(gray2, new Mat(), k2, d2);

        if (d1.empty() || d2.empty()) return Double.MAX_VALUE;

        BFMatcher matcher = BFMatcher.create(BFMatcher.BRUTEFORCE_HAMMING, true);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(d1, d2, matches);

        List<DMatch> matchList = new ArrayList<>(matches.toList());


        ///
        List<Point> srcPointsList = new ArrayList<>();
        List<Point> dstPointsList = new ArrayList<>();

        for (DMatch match : matchList) {
            srcPointsList.add(k1.toList().get(match.queryIdx).pt);
            dstPointsList.add(k2.toList().get(match.trainIdx).pt);
        }
        // Convert lists to MatOfPoint2f
        MatOfPoint2f srcPoints = new MatOfPoint2f();
        MatOfPoint2f dstPoints = new MatOfPoint2f();
        srcPoints.fromList(srcPointsList);
        dstPoints.fromList(dstPointsList);

// Calculate homography with RANSAC
        Mat mask = new Mat();
        if (srcPoints.rows() >= 4) { // Minimum 4 points required
            Calib3d.findHomography(srcPoints, dstPoints,
                Calib3d.RANSAC, 2.0, mask, 2000, 0.995);

            // Optional: Filter matches using mask
            List<DMatch> goodMatches = new ArrayList<>();
            for (int i = 0; i < mask.rows(); i++) {
                if (mask.get(i, 0)[0] == 1) {
                    goodMatches.add(matchList.get(i));
                }
            }

            // Use goodMatches for further processing if needed
            matchList.clear();
            matchList.addAll(goodMatches);
        } else {
            log.warn("Missed homography");
        }
        ///

        Mat debg = new Mat();
        Features2d.drawMatches(gray1, k1, gray2, k2, new MatOfDMatch(matchList.toArray(DMatch[]::new)), debg, Scalar.all(-1),
            Scalar.all(-1), new MatOfByte(), Features2d.DrawMatchesFlags_NOT_DRAW_SINGLE_POINTS);


        // Используем медиану для устойчивости к выбросам
        return matchList.stream()
            .mapToDouble(m -> m.distance)
            .sorted()
            //.limit(matchList.size() / 2 + matchList.size() / 4)//TODO возможно что-то с этим сделать
            .average().stream()
            .map(f -> f / Math.log(1 + matchList.size()))
            .findFirst()
            .orElse(Double.MAX_VALUE);
    }

    private double findScore(List<MatchCandidate> candidates, int slotId, int templateId) {
        for (MatchCandidate candidate : candidates) {
            if (candidate.slotId == slotId && candidate.templateId == templateId) {
                return candidate.combinedScore;
            }
        }
        return Double.MAX_VALUE;
    }

    private record Slot(Rect rect, Mat image, int slotId, Mat prepared) {
    }

    private record MatchCandidate(int slotId, int templateId, double combinedScore, double akazeScore, double orbScore) {
    }

    public record RecognitionResult(Integer id, int quantity, Rect key, int slotId) {
    }

    @Data
    public static final class GlobalRecognitionResult {
        private final int slotId;
        private final int templateId;
        private final Rect rect;
        private final double score;
        private int count;
    }
}
