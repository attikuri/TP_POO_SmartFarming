import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws ThresholdException {

        // ===================================================
        // Helper: build a Date easily
        // ===================================================
        // Day 1 = today, Day 2 = yesterday, Day 3 = 2 days ago
        Calendar cal = Calendar.getInstance();

        cal.set(2026, Calendar.MAY, 9); // 2 days ago
        Date day1 = cal.getTime();

        cal.set(2026, Calendar.MAY, 10); // yesterday
        Date day2 = cal.getTime();

        cal.set(2026, Calendar.MAY, 11); // today
        Date day3 = cal.getTime();

        // ===================================================
        // TEST 1: EnvironmentalSensor (NumericalSensor)
        // ===================================================
        EnvironmentalSensor envSensor = new EnvironmentalSensor(
                54, 10,
                EnvironmentalType.TEMPERATURE,
                15.0, 35.0  // threshold: 15°C to 35°C
        );

        // --- fill history for day1 ---
        Reading[] day1Record = new Reading[24];
        day1Record[0]  = new Reading(18.0, false);
        day1Record[1]  = new Reading(19.5, false);
        day1Record[2]  = new Reading(36.0, true);   // out of range!
        day1Record[3]  = new Reading(22.0, false);
        day1Record[4]  = new Reading(23.0, false);
        day1Record[5]  = new Reading(21.0, false);
        day1Record[6]  = new Reading(20.0, false);
        day1Record[7]  = new Reading(25.0, false);
        day1Record[8]  = new Reading(27.0, false);
        day1Record[9]  = new Reading(29.0, false);
        day1Record[10] = new Reading(31.0, false);
        day1Record[11] = new Reading(33.0, false);
        day1Record[12] = new Reading(34.0, false);
        day1Record[13] = new Reading(34.5, false);
        day1Record[14] = new Reading(33.0, false);
        day1Record[15] = new Reading(30.0, false);
        day1Record[16] = new Reading(28.0, false);
        day1Record[17] = new Reading(26.0, false);
        day1Record[18] = new Reading(24.0, false);
        day1Record[19] = new Reading(22.0, false);
        day1Record[20] = new Reading(20.0, false);
        day1Record[21] = new Reading(19.0, false);
        day1Record[22] = new Reading(18.5, false);
        day1Record[23] = new Reading(18.0, false);
        envSensor.getHistory().put(day1, day1Record);

        // --- fill history for day2 ---
        Reading[] day2Record = new Reading[24];
        for (int i = 0; i < 24; i++) {
            double val = 20.0 + i * 0.5; // values from 20.0 to 31.5
            day2Record[i] = new Reading(val, false);
        }
        envSensor.getHistory().put(day2, day2Record);

        // --- fill history for day3 (today) ---
        Reading[] day3Record = new Reading[24];
        for (int i = 0; i < 24; i++) {
            double val = 14.0 - i * 0.2; // some go below 15 → out of range
            boolean out = val < 15.0 || val > 35.0;
            day3Record[i] = new Reading(val, out);
        }
        envSensor.getHistory().put(day3, day3Record);

        // ===================================================
        // TEST 2: GPSCollar
        // ===================================================
        GPSCollar collar = new GPSCollar(
                50, 7,
                36.5, 37.0,   // lat boundary
                2.8, 3.5      // lon boundary
        );

        // --- fill history for day1 ---
        PositionRecord[] gpsDay1 = new PositionRecord[24];
        for (int i = 0; i < 24; i++) {
            double lat = 36.6 + i * 0.01;
            double lon = 3.0 + i * 0.01;
            boolean out = lat < 36.5 || lat > 37.0 || lon < 2.8 || lon > 3.5;
            gpsDay1[i] = new PositionRecord(new Position(lat, lon), out);
        }
        collar.getHistory().put(day1, gpsDay1);

        // --- fill history for day2 ---
        PositionRecord[] gpsDay2 = new PositionRecord[24];
        for (int i = 0; i < 24; i++) {
            double lat = 36.7 + i * 0.02; // some will exceed 37.0 → out!
            double lon = 3.1;
            boolean out = lat < 36.5 || lat > 37.0 || lon < 2.8 || lon > 3.5;
            gpsDay2[i] = new PositionRecord(new Position(lat, lon), out);
        }
        collar.getHistory().put(day2, gpsDay2);

        // --- fill history for day3 ---
        PositionRecord[] gpsDay3 = new PositionRecord[24];
        for (int i = 0; i < 24; i++) {
            double lat = 36.8;
            double lon = 3.2;
            gpsDay3[i] = new PositionRecord(new Position(lat, lon), false);
        }
        collar.getHistory().put(day3, gpsDay3);

        // ===================================================
        // Sensor card for numerical sensor test
        // ===================================================

        // current day reading
        // add today's readings manually for testing
        envSensor.addReading(0,  18.0);
        envSensor.addReading(1,  19.5);
        envSensor.addReading(2,  36.0);  // out of range → red bar
        envSensor.addReading(3,  22.0);
        envSensor.addReading(4,  23.0);
        envSensor.addReading(5,  21.0);
        envSensor.addReading(6,  20.0);
        envSensor.addReading(7,  25.0);
        envSensor.addReading(8,  27.0);
        envSensor.addReading(9,  29.0);
        envSensor.addReading(10, 31.0);
        envSensor.addReading(11, 33.0);
        envSensor.addReading(12, 34.0);
        envSensor.addReading(13, 34.5);
        envSensor.addReading(14, 33.0);
        envSensor.addReading(15, 30.0);
        envSensor.addReading(16, 28.0);
        envSensor.addReading(17, 26.0);
        envSensor.addReading(18, 24.0);
        envSensor.addReading(19, 22.0);
        envSensor.addReading(20, 20.0);
        envSensor.addReading(21, 19.0);
        envSensor.addReading(22, 18.5);
        envSensor.addReading(23, 18.0);

        SwingUtilities.invokeLater(() -> envSensor.getSensorCard());

        // ===================================================
        // 2. SOIL SENSOR (pH)
        // ===================================================
        SoilSensor soilSensor = new SoilSensor(
                24, 12,
                SoilType.PH,
                5.5, 7.5
        );

        // today's record
        double[] soilToday = {6.0,6.1,6.2,6.3,6.4,6.5,6.6,6.7,6.8,6.9,
                7.0,7.1,7.2,7.3,7.4,7.6,7.7,7.8,5.0,5.2,
                5.4,6.0,6.2,6.5};
        for (int i = 0; i < 24; i++)
            soilSensor.addReading(i, soilToday[i]);

        // history day1
        Reading[] soilDay1 = new Reading[24];
        double[] soilVals1 = {5.5,5.6,5.7,5.8,5.9,6.0,6.1,6.2,6.3,6.4,
                6.5,6.6,6.7,6.8,6.9,7.0,7.1,7.2,7.3,7.4,
                7.5,7.4,7.2,7.0};
        for (int i = 0; i < 24; i++) {
            boolean out = soilVals1[i] < 5.5 || soilVals1[i] > 7.5;
            soilDay1[i] = new Reading(soilVals1[i], out);
        }
        soilSensor.getHistory().put(day1, soilDay1);

        // history day2
        Reading[] soilDay2 = new Reading[24];
        double[] soilVals2 = {7.6,7.5,7.4,7.3,7.2,7.1,7.0,6.9,6.8,6.7,
                6.6,6.5,6.4,6.3,6.2,6.1,6.0,5.9,5.8,5.7,
                5.6,5.5,5.4,5.3};
        for (int i = 0; i < 24; i++) {
            boolean out = soilVals2[i] < 5.5 || soilVals2[i] > 7.5;
            soilDay2[i] = new Reading(soilVals2[i], out);
        }
        soilSensor.getHistory().put(day2, soilDay2);

        // history day3
        Reading[] soilDay3 = new Reading[24];
        double[] soilVals3 = {6.0,6.0,6.1,6.2,6.3,6.4,6.5,6.6,6.7,6.8,
                6.9,7.0,7.1,7.2,7.3,7.4,7.5,7.6,7.4,7.2,
                7.0,6.8,6.5,6.2};
        for (int i = 0; i < 24; i++) {
            boolean out = soilVals3[i] < 5.5 || soilVals3[i] > 7.5;
            soilDay3[i] = new Reading(soilVals3[i], out);
        }
        soilSensor.getHistory().put(day3, soilDay3);

        // ===================================================
        // 3. WATER SENSOR (Dissolved Oxygen)
        // ===================================================
        WaterSensor waterSensor = new WaterSensor(
                114, 05,
                WaterType.DISSOLVED_O2,
                6.0, 12.0
        );

        // today's record
        double[] waterToday = {8.0,8.1,8.2,8.3,8.4,8.5,8.6,8.7,8.8,8.9,
                9.0,9.1,9.2,9.3,9.4,9.5,5.5,5.8,9.0,9.1,
                9.2,9.3,9.4,9.5};
        for (int i = 0; i < 24; i++)
            waterSensor.addReading(i, waterToday[i]);

        // history day1
        Reading[] waterDay1 = new Reading[24];
        double[] waterVals1 = {7.0,7.2,7.4,7.6,7.8,8.0,8.2,8.4,8.6,8.8,
                9.0,9.2,9.4,9.6,9.8,10.0,10.2,10.4,10.6,10.8,
                11.0,11.2,11.4,11.6};
        for (int i = 0; i < 24; i++) {
            boolean out = waterVals1[i] < 6.0 || waterVals1[i] > 12.0;
            waterDay1[i] = new Reading(waterVals1[i], out);
        }
        waterSensor.getHistory().put(day1, waterDay1);

        // history day2
        Reading[] waterDay2 = new Reading[24];
        double[] waterVals2 = {11.0,10.8,10.5,10.2,9.9,9.6,9.3,9.0,8.7,8.4,
                8.1,7.8,7.5,7.2,6.9,6.6,6.3,6.0,5.7,5.4,
                8.0,8.5,9.0,9.5};
        for (int i = 0; i < 24; i++) {
            boolean out = waterVals2[i] < 6.0 || waterVals2[i] > 12.0;
            waterDay2[i] = new Reading(waterVals2[i], out);
        }
        waterSensor.getHistory().put(day2, waterDay2);

        // history day3
        Reading[] waterDay3 = new Reading[24];
        double[] waterVals3 = {8.5,8.6,8.7,8.8,8.9,9.0,9.1,9.2,9.3,9.4,
                9.5,9.6,9.7,9.8,9.9,10.0,5.0,5.2,9.5,9.6,
                9.7,9.8,9.9,10.0};
        for (int i = 0; i < 24; i++) {
            boolean out = waterVals3[i] < 6.0 || waterVals3[i] > 12.0;
            waterDay3[i] = new Reading(waterVals3[i], out);
        }
        waterSensor.getHistory().put(day3, waterDay3);

        // ===================================================
        // 4. BIOMETRIC SENSOR (Activity)
        // ===================================================
        BiometricSensor bioSensor = new BiometricSensor(
                50, 19,
                BiometricType.ACTIVITY,
                10.0, 80.0
        );

        // today's record
        double[] bioToday = {15,20,25,30,35,40,45,50,55,60,
                65,70,75,80,85,78,70,60,50,40,
                30,20,15,10};
        for (int i = 0; i < 24; i++)
            bioSensor.addReading(i, bioToday[i]);

        // history day1
        Reading[] bioDay1 = new Reading[24];
        double[] bioVals1 = {10,15,20,25,30,35,40,45,50,55,
                60,65,70,75,80,82,75,65,55,45,
                35,25,18,12};
        for (int i = 0; i < 24; i++) {
            boolean out = bioVals1[i] < 10 || bioVals1[i] > 80;
            bioDay1[i] = new Reading(bioVals1[i], out);
        }
        bioSensor.getHistory().put(day1, bioDay1);

        // history day2
        Reading[] bioDay2 = new Reading[24];
        double[] bioVals2 = {12,18,24,30,36,42,48,54,60,66,
                72,78,84,80,75,70,65,60,50,40,
                30,22,16,11};
        for (int i = 0; i < 24; i++) {
            boolean out = bioVals2[i] < 10 || bioVals2[i] > 80;
            bioDay2[i] = new Reading(bioVals2[i], out);
        }
        bioSensor.getHistory().put(day2, bioDay2);

        // history day3
        Reading[] bioDay3 = new Reading[24];
        double[] bioVals3 = {11,16,22,28,34,40,46,52,58,64,
                70,76,80,79,74,68,62,55,48,40,
                32,24,17,12};
        for (int i = 0; i < 24; i++) {
            boolean out = bioVals3[i] < 10 || bioVals3[i] > 80;
            bioDay3[i] = new Reading(bioVals3[i], out);
        }
        bioSensor.getHistory().put(day3, bioDay3);

        // ===================================================
        // LAUNCH ALL 4 CARDS
        // ===================================================
        SwingUtilities.invokeLater(() -> {
            soilSensor.getSensorCard();  // card 2
            waterSensor.getSensorCard(); // card 3
            bioSensor.getSensorCard();   // card 4
        });



    }
}