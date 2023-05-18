import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Analyzer {
    private static int aNumber = 0;
    private static int bNumber = 0;
    private static int cNumber = 0;
    private static int textCount = 0;
    private static final int numberOfTexts = 10_000;
    private static final int length = 100_000;
    private static final String letters = "abc";
    private static final int queueCapacity = 100;
    private static final BlockingQueue<String> aArrayBlockingQueue = new ArrayBlockingQueue<>(queueCapacity);
    private static final BlockingQueue<String> bArrayBlockingQueue = new ArrayBlockingQueue<>(queueCapacity);
    private static final BlockingQueue<String> cArrayBlockingQueue = new ArrayBlockingQueue<>(queueCapacity);

    public static void main(String[] a) throws InterruptedException {
        Thread stringFiller = new Thread(() -> {
            while (textCount < numberOfTexts) {
                String string = generateText(letters, length);
                textCount = textCount + 1;
                try {
                    aArrayBlockingQueue.put(string);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    bArrayBlockingQueue.put(string);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    cArrayBlockingQueue.put(string);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        stringFiller.start();
        Thread aCounter = new Thread(() -> {
            processStrings('a', aNumber, (ArrayBlockingQueue) aArrayBlockingQueue);
        });
        aCounter.start();
        Thread bCounter = new Thread(() -> {
            processStrings('b', bNumber, (ArrayBlockingQueue) bArrayBlockingQueue);
        });
        bCounter.start();
        Thread cCounter = new Thread(() -> {
            processStrings('c', cNumber, (ArrayBlockingQueue) cArrayBlockingQueue);
        });
        cCounter.start();
        stringFiller.join();
        aCounter.join();
        bCounter.join();
        cCounter.join();
        System.out.println("Максимальное количество букв \"a\" в одной строке: " + aNumber);
        System.out.println("Максимальное количество букв \"b\" в одной строке: " + bNumber);
        System.out.println("Максимальное количество букв \"c\" в одной строке: " + cNumber);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static void processStrings(char chr, int xNumber, ArrayBlockingQueue arrayBlockingQueue) {
        int number;
        while (textCount < numberOfTexts) {
            try {
                String string = (String) arrayBlockingQueue.take();
                number = Math.toIntExact(string.chars().filter(ch -> ch == chr).count());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (number > xNumber) {
                switch (chr) {
                    case ('a'):
                        aNumber = number;
                        break;
                    case ('b'):
                        bNumber = number;
                        break;
                    case ('c'):
                        cNumber = number;
                        break;
                }
            }
        }
    }
}