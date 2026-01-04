package ru.demetrious.deus.bot.app.impl.command.test;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
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
import org.opencv.features2d.ORB;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Integer.parseInt;
import static java.lang.Math.divideExact;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.opencv.calib3d.Calib3d.RANSAC;
import static org.opencv.calib3d.Calib3d.findHomography;
import static org.opencv.core.Core.CMP_EQ;
import static org.opencv.core.Core.CMP_GT;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.Core.bitwise_or;
import static org.opencv.core.Core.compare;
import static org.opencv.core.Core.extractChannel;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.Mat.zeros;
import static org.opencv.features2d.BFMatcher.BRUTEFORCE_HAMMING;
import static org.opencv.features2d.BFMatcher.create;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2BGRA;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;
import static org.opencv.imgproc.Imgproc.GC_FGD;
import static org.opencv.imgproc.Imgproc.GC_INIT_WITH_RECT;
import static org.opencv.imgproc.Imgproc.GC_PR_BGD;
import static org.opencv.imgproc.Imgproc.GC_PR_FGD;
import static org.opencv.imgproc.Imgproc.HISTCMP_BHATTACHARYYA;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.calcHist;
import static org.opencv.imgproc.Imgproc.compareHist;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.grabCut;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static ru.demetrious.deus.bot.app.impl.command.Reverse1999MaterialsSetCommandUseCase.toBufferedImage;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfException;
import static ru.demetrious.deus.bot.utils.ImageUtils.toMat;

@Slf4j
public class MaterialRecognizer {
    private static final double MATCH_THRESHOLD = 500.0;
    private static final Feature2D AKAZE_DETECTOR = AKAZE.create();
    private static final Feature2D ORB_DETECTOR = ORB.create();

    @NotNull
    private final Tesseract tesseract;
    @NotNull
    private final Map<Integer, Mat> templates;
    private final List<Slot> allSlots = new ArrayList<>();

    public MaterialRecognizer(@NotNull Tesseract tesseract, @NotNull Map<Integer, ItemData> items) {
        this.tesseract = tesseract;
        this.templates = items.entrySet().stream()
            .filter(entry -> nonNull(entry.getValue().getImage()))
            .collect(toMap(Map.Entry::getKey, entry -> prepare(toMat(entry.getValue().getImage().toBufferedImage()))));

    }

    public void collectSlot(Mat slot) {
        allSlots.add(new Slot(allSlots.size(), prepare(slot), prepareForOcr(slot)));
    }

    public Map<Integer, Integer> recognize() {
        if (isEmpty(allSlots)) {
            return Map.of();
        }

        AtomicInteger atomicInteger = new AtomicInteger();
        List<MatchCandidate> candidates = new ArrayList<>();
        Map<Integer, Integer> pairs = new HashMap<>();
        AtomicDouble percent = new AtomicDouble();

        allSlots.forEach(slot -> templates.forEach((templateId, template) -> {
            double akazeScore = compareWithAKAZE(slot.recognizing(), template);
            double orbScore = compareWithORB(slot.recognizing(), template);
            double histDist = compareHistograms(slot.recognizing(), template);
            double combinedScore = (akazeScore + orbScore) * (2 + Math.pow(histDist, 2)) / 6;

            int counter = atomicInteger.incrementAndGet();
            if (divideExact(100 * counter, allSlots.size() * templates.size()) != percent.get()) {
                int newPercent = divideExact(100 * counter, allSlots.size() * templates.size());

                percent.set(newPercent);
                log.info("Process: {}%", newPercent);
            }

            if (combinedScore >= MAX_VALUE / 2) {
                return;
            }

            candidates.add(new MatchCandidate(slot.slotId(), templateId, combinedScore));
        }));
        candidates.sort(comparingDouble(MatchCandidate::score));
        candidates.forEach(matchCandidate -> {
            if (pairs.containsKey(matchCandidate.slotId()) || pairs.containsValue(matchCandidate.templateId()) || matchCandidate.score() >= MATCH_THRESHOLD) {
                return;
            }

            pairs.put(matchCandidate.slotId(), matchCandidate.templateId());
        });

        return allSlots.stream()
            .filter(slot -> pairs.containsKey(slot.slotId()))
            .collect(toMap(slot -> pairs.get(slot.slotId()), this::doOcr));
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private int doOcr(Slot slot) {
        return defaultIfException(() -> parseInt(tesseract.doOCR(toBufferedImage(slot.ocr())).trim()), 0);
    }

    private Mat prepare(Mat source) {
        Mat cloned = source.clone();
        Mat mask = new Mat(cloned.size(), CV_8UC1, new Scalar(GC_PR_BGD));
        int rectW = (int) (cloned.width() * 0.8);
        int rectH = (int) (cloned.height() * 0.8);
        Rect rect = new Rect((cloned.width() - rectW) / 2, (cloned.height() - rectH) / 2, rectW, rectH);
        Mat foregroundMask = new Mat(mask.size(), CV_8UC1);
        Mat sureFg = new Mat();
        Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(5, 5));
        Mat result = new Mat(cloned.size(), CvType.CV_8UC4);
        int borderMargin = (int) (cloned.height() * 0.05);
        int textPanelHeight = (int) (cloned.height() * 0.25);
        Rect topMask = new Rect(borderMargin, borderMargin, cloned.width() - 2 * borderMargin, cloned.height() - borderMargin - textPanelHeight);

        grabCut(cloned, mask, rect, new Mat(), new Mat(), 5, GC_INIT_WITH_RECT);
        compare(mask, new Scalar(GC_PR_FGD), foregroundMask, CMP_EQ);
        compare(mask, new Scalar(GC_FGD), sureFg, CMP_EQ);
        bitwise_or(foregroundMask, sureFg, foregroundMask);
        morphologyEx(foregroundMask, foregroundMask, MORPH_CLOSE, kernel);
        cvtColor(cloned, cloned, COLOR_BGR2BGRA);

        for (int y = 0; y < cloned.rows(); y++) {
            for (int x = 0; x < cloned.cols(); x++) {
                double f = foregroundMask.get(y, x)[0];
                double[] pixel = cloned.get(y, x);

                pixel[3] = f;
                result.put(y, x, pixel);
            }
        }

        return new Mat(result, topMask);
    }

    private Mat prepareForOcr(Mat source) {
        Rect rect = new Rect(0, source.height() - (int) (source.height() * 0.25), source.width(), (int) (source.height() * 0.2));
        Mat cloned = new Mat(source, rect);
        Mat hsv = new Mat();
        Scalar lowerWhite = new Scalar(0, 0, 220);
        Scalar upperWhite = new Scalar(180, 30, 255);
        Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(5, 5));
        Mat mask = new Mat();
        Mat result = zeros(cloned.size(), cloned.type());

        cvtColor(cloned, hsv, COLOR_BGR2HSV);
        inRange(hsv, lowerWhite, upperWhite, mask);
        dilate(mask, mask, kernel);
        cloned.copyTo(result, mask);
        bitwise_not(result, result);

        return result;
    }

    private static double compareWithAKAZE(Mat img1, Mat img2) {
        return compareWithDetector(img1, img2, AKAZE_DETECTOR);
    }

    private static double compareWithORB(Mat img1, Mat img2) {
        return compareWithDetector(img1, img2, ORB_DETECTOR);
    }

    private static double compareWithDetector(Mat img1, Mat img2, Feature2D detector) {
        Mat gray1 = new Mat();
        Mat gray2 = new Mat();
        MatOfKeyPoint k1 = new MatOfKeyPoint();
        MatOfKeyPoint k2 = new MatOfKeyPoint();
        Mat d1 = new Mat();
        Mat d2 = new Mat();

        cvtColor(img1, gray1, COLOR_BGRA2GRAY);
        cvtColor(img2, gray2, COLOR_BGRA2GRAY);
        detector.detectAndCompute(gray1, new Mat(), k1, d1);
        detector.detectAndCompute(gray2, new Mat(), k2, d2);

        if (d1.empty() || d2.empty()) {
            return MAX_VALUE;
        }

        BFMatcher matcher = create(BRUTEFORCE_HAMMING, true);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(d1, d2, matches);
        List<DMatch> matchList = filterByHomography(matches, k1.toList(), k2.toList());

        return matchList.stream()
            .mapToDouble(dMatch -> dMatch.distance)
            .sorted()
            .average().stream()
            .map(averageDistance -> averageDistance / Math.log(1 + matchList.size()))
            .findFirst()
            .orElse(MAX_VALUE);
    }

    private static List<DMatch> filterByHomography(MatOfDMatch matches, List<KeyPoint> k1List, List<KeyPoint> k2List) {
        List<DMatch> matchList = new ArrayList<>(matches.toList());
        List<Point> srcPointsList = new ArrayList<>();
        List<Point> dstPointsList = new ArrayList<>();
        MatOfPoint2f srcPoints = new MatOfPoint2f();
        MatOfPoint2f dstPoints = new MatOfPoint2f();
        Mat mask = new Mat();

        matchList.forEach(match -> {
            srcPointsList.add(k1List.get(match.queryIdx).pt);
            dstPointsList.add(k2List.get(match.trainIdx).pt);
        });
        srcPoints.fromList(srcPointsList);
        dstPoints.fromList(dstPointsList);

        if (srcPoints.rows() < 4) {
            return matchList;
        }

        findHomography(srcPoints, dstPoints, RANSAC, 2.0, mask, 2000, 0.995);
        return range(0, mask.rows())
            .filter(i -> mask.get(i, 0)[0] == 1)
            .mapToObj(matchList::get)
            .toList();
    }

    private static double compareHistograms(Mat img1, Mat img2) {
        Mat hsv1 = new Mat();
        Mat hsv2 = new Mat();
        Mat mask1 = new Mat();
        Mat mask2 = new Mat();
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();

        cvtColor(img1, hsv1, COLOR_BGR2HSV);
        cvtColor(img2, hsv2, COLOR_BGR2HSV);
        extractChannel(img1, mask1, 3);
        extractChannel(img2, mask2, 3);
        compare(mask1, new Scalar(0), mask1, CMP_GT);
        compare(mask2, new Scalar(0), mask2, CMP_GT);
        calcHist(singletonList(hsv1), new MatOfInt(0, 1), mask1, hist1, new MatOfInt(32, 32), new MatOfFloat(0f, 180f, 0f, 256f));
        calcHist(singletonList(hsv2), new MatOfInt(0, 1), mask2, hist2, new MatOfInt(32, 32), new MatOfFloat(0f, 180f, 0f, 256f));
        normalize(hist1, hist1, 0, 1, NORM_MINMAX);
        normalize(hist2, hist2, 0, 1, NORM_MINMAX);

        return compareHist(hist1, hist2, HISTCMP_BHATTACHARYYA);
    }

    private record Slot(int slotId, Mat recognizing, Mat ocr) {
    }

    private record MatchCandidate(int slotId, int templateId, double score) {
    }
}
