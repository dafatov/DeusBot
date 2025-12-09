package ru.demetrious.deus.bot.utils.reverse;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;
import ru.demetrious.deus.bot.domain.reverse1999.LevelData;
import ru.demetrious.deus.bot.domain.reverse1999.ReverseData;

import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.app.impl.canvas.ReverseMaterialsCanvas.Item;
import static ru.demetrious.deus.bot.app.impl.canvas.ReverseMaterialsCanvas.Level;

public class FarmingPlanUtils {
    public static List<Level> calculateFarmingPlan(Map<Integer, Integer> targetMaterials, ReverseData reverseData) {
        // Карты для отслеживания результатов
        Map<Integer, Double> minCost = new HashMap<>();
        Map<Integer, Object> bestSource = new HashMap<>();
        Map<Integer, Integer> impossibleItems = new HashMap<>();
        Map<Integer, Integer> materialsToFarm = new HashMap<>();
        Map<Integer, Integer> craftsToDo = new HashMap<>();

        // Шаг 1: Инициализация предметов, которые можно получить фармом
        for (Map.Entry<Integer, LevelData> levelEntry : reverseData.getLevels().entrySet()) {
            LevelData level = levelEntry.getValue();
            for (Map.Entry<Integer, LevelData.Drop> dropEntry : level.getDropMap().entrySet()) {
                int itemId = dropEntry.getKey();
                Double expectation = dropEntry.getValue().getMathematicalExpectation();
                if (expectation != null && expectation > 0) {
                    double costPerItem = level.getCost() / expectation;
                    if (!minCost.containsKey(itemId) || costPerItem < minCost.get(itemId)) {
                        minCost.put(itemId, costPerItem);
                        bestSource.put(itemId, level);
                    }
                }
            }
        }

        // Шаг 2: Алгоритм Беллмана-Форда для учета крафта
        boolean changed;
        int maxIterations = reverseData.getItems().size();
        int iteration = 0;

        do {
            changed = false;
            iteration++;

            for (Map.Entry<Integer, ItemData> itemEntry : reverseData.getItems().entrySet()) {
                int itemId = itemEntry.getKey();
                ItemData itemData = itemEntry.getValue();
                Map<Integer, Integer> craft = itemData.getCraft();

                if (!craft.isEmpty()) {
                    double craftCost = 0;
                    boolean canCraft = true;

                    // Вычисляем стоимость крафта
                    for (Map.Entry<Integer, Integer> materialEntry : craft.entrySet()) {
                        int materialId = materialEntry.getKey();
                        int amount = materialEntry.getValue();

                        if (!minCost.containsKey(materialId)) {
                            canCraft = false;
                            break;
                        }
                        craftCost += minCost.get(materialId) * amount;
                    }

                    // Если можно скрафтить и это выгоднее текущего способа
                    if (canCraft) {
                        double currentCost = minCost.getOrDefault(itemId, Double.MAX_VALUE);
                        if (craftCost < currentCost) {
                            minCost.put(itemId, craftCost);
                            bestSource.put(itemId, craft);
                            changed = true;
                        }
                    }
                }
            }
        } while (changed && iteration < maxIterations);

        // Шаг 3: Разбор целевых предметов
        for (Map.Entry<Integer, Integer> targetEntry : targetMaterials.entrySet()) {
            int itemId = targetEntry.getKey();
            int amount = targetEntry.getValue();

            if (!reverseData.getItems().containsKey(itemId)) {
                // Предмет не существует в базе данных
                impossibleItems.put(itemId, impossibleItems.getOrDefault(itemId, 0) + amount);
                continue;
            }

            decomposeItem(itemId, amount, minCost, bestSource, materialsToFarm, craftsToDo, impossibleItems);
        }

        // Шаг 4: Оптимизированный выбор уровней для фарма
        Map<LevelData, Integer> levelRuns = optimizeLevelSelection(materialsToFarm, reverseData, minCost);

        // Шаг 5: Формирование результата
        List<Level> result = new ArrayList<>();

        // Уровни для фарма
        for (Map.Entry<LevelData, Integer> entry : levelRuns.entrySet()) {
            LevelData level = entry.getKey();
            int runs = entry.getValue();
            List<Item> items = new ArrayList<>();

            for (Map.Entry<Integer, LevelData.Drop> dropEntry : level.getDropMap().entrySet()) {
                int dropItemId = dropEntry.getKey();
                Double expectation = dropEntry.getValue().getMathematicalExpectation();
                if (expectation != null && expectation > 0) {
                    BufferedImage image = reverseData.getItems().get(dropItemId) != null ?
                        reverseData.getItems().get(dropItemId).getImage().toBufferedImage() : null;
                    items.add(new Item(image, expectation * runs));
                }
            }

            result.add(new Level(level.getName(), level.getCost() * runs, runs, items));
        }

        if (!craftsToDo.isEmpty()) {
            result.add(new Level("Wishing Spring", 0, 0, convertToItems(craftsToDo, reverseData)));
        }
        if (!impossibleItems.isEmpty()) {
            result.add(new Level("???", 0, 0, convertToItems(impossibleItems, reverseData)));
        }
        return result;
    }

    private static List<Item> convertToItems(Map<Integer, Integer> itemsMap, ReverseData reverseData) {
        return itemsMap.entrySet().stream()
            .map(entry -> new Item(ofNullable(reverseData.getItems().getOrDefault(entry.getKey(), null))
                .map(ItemData::getImage)
                .map(Image::toBufferedImage)
                .orElse(null), entry.getValue()))
            .toList();
    }

    // Рекурсивное разложение предметов с заполнением impossibleItems
    private static void decomposeItem(int itemId, int amount,
                                      Map<Integer, Double> minCost,
                                      Map<Integer, Object> bestSource,
                                      Map<Integer, Integer> materialsToFarm,
                                      Map<Integer, Integer> craftsToDo,
                                      Map<Integer, Integer> impossibleItems) {
        // Если предмет невозможно получить никаким способом
        if (!minCost.containsKey(itemId)) {
            impossibleItems.put(itemId, impossibleItems.getOrDefault(itemId, 0) + amount);
            return;
        }

        Object source = bestSource.get(itemId);
        if (source instanceof LevelData) {
            // Предмет получаем фармом
            materialsToFarm.put(itemId, materialsToFarm.getOrDefault(itemId, 0) + amount);
        } else if (source instanceof Map) {
            // Предмет получаем крафтом
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> craftRecipe = (Map<Integer, Integer>) source;
            craftsToDo.put(itemId, craftsToDo.getOrDefault(itemId, 0) + amount);

            // Рекурсивно разбираем материалы для крафта
            for (Map.Entry<Integer, Integer> materialEntry : craftRecipe.entrySet()) {
                int materialId = materialEntry.getKey();
                int materialAmount = materialEntry.getValue() * amount;
                decomposeItem(materialId, materialAmount, minCost, bestSource, materialsToFarm,
                    craftsToDo, impossibleItems);
            }
        } else {
            // Неизвестный способ получения - считаем невозможным
            impossibleItems.put(itemId, impossibleItems.getOrDefault(itemId, 0) + amount);
        }
    }

    // Улучшенный алгоритм выбора уровней
    private static Map<LevelData, Integer> optimizeLevelSelection(Map<Integer, Integer> materialsToFarm,
                                                                  ReverseData reverseData,
                                                                  Map<Integer, Double> minCost) {
        Map<LevelData, Integer> levelRuns = new HashMap<>();
        Map<Integer, Double> remainingNeeds = new HashMap<>();

        // Инициализация остаточных потребностей
        for (Map.Entry<Integer, Integer> entry : materialsToFarm.entrySet()) {
            remainingNeeds.put(entry.getKey(), (double) entry.getValue());
        }

        // Пока есть неудовлетворенные потребности
        while (true) {
            LevelData bestLevel = null;
            double bestEfficiency = -1;

            // Поиск наиболее эффективного уровня
            for (LevelData level : reverseData.getLevels().values()) {
                double totalValue = 0;
                int usefulDrops = 0;

                // Вычисляем ценность уровня на основе нужных предметов
                for (Map.Entry<Integer, LevelData.Drop> dropEntry : level.getDropMap().entrySet()) {
                    int itemId = dropEntry.getKey();
                    if (remainingNeeds.containsKey(itemId) && remainingNeeds.get(itemId) > 0) {
                        Double expectation = dropEntry.getValue().getMathematicalExpectation();
                        if (expectation != null && expectation > 0) {
                            double itemValue = minCost.getOrDefault(itemId, 0d);
                            totalValue += expectation * itemValue;
                            usefulDrops++;
                        }
                    }
                }

                if (totalValue > 0) {
                    double efficiency = totalValue / level.getCost();
                    // Предпочтение уровням с большим количеством полезных дропов
                    efficiency *= (1 + usefulDrops * 0.1);

                    if (efficiency > bestEfficiency) {
                        bestEfficiency = efficiency;
                        bestLevel = level;
                    }
                }
            }

            if (bestLevel == null) {
                break; // Нет больше подходящих уровней
            }

            // Определяем, сколько раз пройти уровень
            int runsForThisLevel = calculateOptimalRuns(bestLevel, remainingNeeds);
            if (runsForThisLevel == 0) {
                break;
            }

            // Добавляем прогоны уровня
            levelRuns.put(bestLevel, levelRuns.getOrDefault(bestLevel, 0) + runsForThisLevel);

            // Уменьшаем остаточные потребности
            for (Map.Entry<Integer, LevelData.Drop> dropEntry : bestLevel.getDropMap().entrySet()) {
                int itemId = dropEntry.getKey();
                if (remainingNeeds.containsKey(itemId)) {
                    Double expectation = dropEntry.getValue().getMathematicalExpectation();
                    if (expectation != null && expectation > 0) {
                        double gathered = expectation * runsForThisLevel;
                        double newAmount = remainingNeeds.get(itemId) - gathered;
                        if (newAmount <= 0) {
                            remainingNeeds.remove(itemId);
                        } else {
                            remainingNeeds.put(itemId, newAmount);
                        }
                    }
                }
            }

            // Проверяем, остались ли потребности
            if (remainingNeeds.isEmpty()) {
                break;
            }
        }

        return levelRuns;
    }

    // Расчет оптимального количества прогонов для уровня
    private static int calculateOptimalRuns(LevelData level, Map<Integer, Double> remainingNeeds) {
        int minRuns = Integer.MAX_VALUE;

        for (Map.Entry<Integer, LevelData.Drop> dropEntry : level.getDropMap().entrySet()) {
            int itemId = dropEntry.getKey();
            if (remainingNeeds.containsKey(itemId)) {
                Double expectation = dropEntry.getValue().getMathematicalExpectation();
                if (expectation != null && expectation > 0) {
                    double needed = remainingNeeds.get(itemId);
                    int runsForItem = (int) Math.ceil(needed / expectation);
                    minRuns = Math.min(minRuns, runsForItem);
                }
            }
        }

        // Если на уровне есть только один нужный предмет, фармим до удовлетворения потребности
        // Иначе ограничиваемся разумным количеством чтобы не перефармить
        int usefulDrops = 0;
        for (Map.Entry<Integer, LevelData.Drop> dropEntry : level.getDropMap().entrySet()) {
            int itemId = dropEntry.getKey();
            if (remainingNeeds.containsKey(itemId)) {
                usefulDrops++;
            }
        }

        if (usefulDrops == 1) {
            return minRuns;
        } else {
            // Для уровней с множественными дропами ограничиваем прогоны
            return Math.min(minRuns, 10); // Максимум 10 прогонов за раз
        }
    }
}
