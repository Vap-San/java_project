import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ToyLottery {
    public static void main(String[] args) throws FileNotFoundException {

        // Так и получись вводить игрушки русскими буквами - пишет вместо них знаки ?
        // Пробовал так - не получил результата
        // String encoding = System.getProperty("console.encoding", "utf-8");
        // Scanner scanner = new Scanner(System.in, encoding);
        Scanner scanner = new Scanner(System.in);

        Map<Integer, Toy> toys = new HashMap<>();
        boolean running = true;
        readToysFile(toys);
        while (running) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Добавить новую игрушку");
            System.out.println("2. Вывести список игрушек");
            System.out.println("3. Изменить количество игрушки");
            System.out.println("4. Начать розыгрыш игрушек");
            System.out.println("5. Выйти");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addNewToy(scanner, toys);
                    break;
                case 2:
                    showToys(toys);
                    break;
                case 3:
                    changeToyQuantity(scanner, toys);
                    break;
                case 4:
                    startLottery(scanner, toys);
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Неверный выбор");
            }
        }
        scanner.close();
    }

    // Чтение из файла стартового набора игрушек
    private static void readToysFile(Map<Integer, Toy> toys) throws FileNotFoundException {
        File file = new File("toys.txt");
        Scanner scn = new Scanner(file);
        // Map<Integer, Toy> toys = new HashMap<>();
        while (scn.hasNextLine()) {
            String line = scn.nextLine();
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            int quantity = Integer.parseInt(parts[2]);
            Toy toy = new Toy(id, name, quantity);
            toys.put(id, toy);
        }
        showToys(toys);
        scn.close();
    }

    // Вывод списка игрушек на экран
    private static void showToys(Map<Integer, Toy> toys) {
        List<Toy> toyList = new ArrayList<>(toys.values());
        calculateProbability(toyList);
        System.out.println("\nСписок игрушек: \n");
        for (Toy toy : toyList) {
            System.out.println(toy.getId() + ": " + toy.getName() + ", количество - " + toy.getQuantity()
                    + ", вероятности быть выбранной - " + toy.getFrequency());
        }
    }

    // Добавление новой игрушки в список
    private static void addNewToy(Scanner scanner, Map<Integer, Toy> toys) {
        scanner.nextLine();
        System.out.print("Введите название игрушки: ");
        String name = scanner.nextLine();
        System.out.print("Введите количество: ");
        int quantity = scanner.nextInt();
        int id = toys.size() + 1;
        Toy toy = new Toy(id, name, quantity);
        toys.put(id, toy);

        System.out.println("Игрушка " + name + " добавлена");
    }

    // Изменение количества у игрушки с дальнейшим перерасчетом вероятности быть
    // выбранной
    private static void changeToyQuantity(Scanner scanner, Map<Integer, Toy> toys) {
        System.out.print("Введите ID игрушки: ");
        int id = scanner.nextInt();

        if (toys.containsKey(id)) {
            System.out.print("Введите новое количество игрушки: ");
            int quantity = scanner.nextInt();
            // извлекает объект игрушки из списка по указанному индексу и сохраняет его в
            // переменной "toy".
            Toy toy = toys.get(id);
            toy.setQuantity(quantity);
            List<Toy> toyList = new ArrayList<>(toys.values());
            calculateProbability(toyList); // Изменилось количество - пересчитываем процент вероятности выбора игрушки
                                           // при лотереи
            System.out.println("Количество игрушки " + toy.getName() + " изменено на " + quantity
                    + ", % вероятности быть выбранной изменен на " + toy.getFrequency());
        } else {
            System.out.println("Игрушки с ID " + id + " не существует");
        }
    }

    // Проведение лотереи
    private static void startLottery(Scanner scanner, Map<Integer, Toy> toys) {
        System.out.print("Введите количество выигрышных игрушек: ");
        int numWinningToys = scanner.nextInt();

        List<Toy> toyList = new ArrayList<>(toys.values());
        calculateProbability(toyList);

        List<Toy> winningToys = new ArrayList<>();
        Random random = new Random();

        // чтобы быстрее выявить победителей, можно уменьшить случайное число
        // double randomFrequency = 100 * random.nextDouble() / 2;

        double randomFrequency = 100 * random.nextDouble();
        System.out.println("Случайный процент: " + randomFrequency + "\n");
        boolean rezult = false;
        int count = 0;
        for (Toy toy : toyList) {
            if (toy.getFrequency() >= randomFrequency && count < numWinningToys) {
                winningToys.add(toy);
                toy.setQuantity(toy.getQuantity() - 1);
                count += 1;
                rezult = true;
            }
        }
        if (rezult) {
            System.out.println("Выигрышные игрушки:");
            for (Toy toy : winningToys) {
                System.out.println(toy.getName());
            }
            writerFileFirstToy(winningToys);
        } else {
            System.out.println("Выигрышных игрушек нет, повторите лотерею");
        }
    }

    // Расчитываем вероятность выбора игрушек в лотереи
    private static void calculateProbability(List<Toy> toyList) {
        int totalQuantity = 0;
        for (Toy toy : toyList) {
            totalQuantity += toy.getQuantity();
        }
        for (Toy toy : toyList) {
            toy.setFrequency(totalQuantity);
        }
    }

    // Записваем в файл первого из списка победителей, сдвигаем спсиок на 1 влево
    private static void writerFileFirstToy(List<Toy> winningToys) {
        try {
            // выбираем первый элемент из winningToys
            Toy firstToy = winningToys.get(0);

            // записываем его в текстовый файл
            FileWriter writer = new FileWriter("output.txt");
            writer.write(firstToy.getId() + " - " + firstToy.getName());
            writer.close();
            System.out.println("\nИгрушка выдана (перенесена в файл): ");
            System.out.println(firstToy.getId() + " - " + firstToy.getName());

            // смещаем все элементы на один элемент к началу списка
            for (int i = 0; i < winningToys.size() - 1; i++) {
                winningToys.set(i, winningToys.get(i + 1));
            }
            winningToys.remove(winningToys.size() - 1);

            System.out.println("\nОстались следующие игрушки- призы: ");
            for (Toy toy : winningToys) {
                System.out.println(toy.getName());
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл");
        }
    }
}
