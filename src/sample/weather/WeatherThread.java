package sample.weather;

import sample.model.Observable;
import sample.model.Observer;

import java.util.ArrayList;

public class WeatherThread implements Runnable, Observable {

    private Thread thread;
    private int interval;
    private volatile boolean isRunning = false;
    private volatile ArrayList<Observer> observerList = new ArrayList<>();


    public WeatherThread() {
        interval = 6000;
    }

    public WeatherThread(int interval) {
        this.interval = interval;

    }

    public int getInterval() {
        return interval;
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observerList.contains(observer)) {
            observerList.remove(observer);
        }
    }

    @Override
    public void updateObservers() {
        for (Observer o : observerList) {
            Weather weather = WeatherStation.getWeatherFromCity(o.getCity());
            //Weather weather = new Weather(0,1024,92,-2,10);
            o.updateWeather(weather);
        }
    }

    public void start() {
        thread = new Thread(this, "Weather Thread");
        thread.start();
    }

    @Override
    public void run() {
        isRunning = true;

        while (isRunning) {
            try {
                updateObservers();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Failed");
            }
        }

    }


}