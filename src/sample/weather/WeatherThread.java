package sample.weather;

import sample.Controller;
import sample.model.Observable;
import sample.model.Observer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that represents thread where weather is updating.
 * Updating observers which are added to this thread.
 */

public class WeatherThread implements Runnable, Observable{

    private Thread thread;
    private int interval;
    private volatile boolean isRunning = false;
    private volatile ArrayList<Observer> observerList = new ArrayList<>();
    private boolean updatedObserver = false;


    public WeatherThread() {
        interval = 60000;
    }

    public WeatherThread(int interval) {
        this.interval = interval;

    }

    /**
     *
     * @return Sleep interval
     */
    public int getInterval() {
        return interval;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isUpdatedObserver() {
        return updatedObserver;
    }

    public void setUpdatedObserver(boolean updatedObserver) {
        this.updatedObserver = updatedObserver;
    }

    /**
     * Adding new observer that observing thread
     * @param observer
     */
    @Override
    public void addObserver(Observer observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    /**
     * Remove existing observer.
     * @param observer
     */
    @Override
    public void removeObserver(Observer observer) {
        if (observerList.contains(observer)) {
            observerList.remove(observer);
        }
    }

    @Override
    public void updateObservers() throws IOException {
        for (Observer o : observerList) {
            Weather weather = WeatherStation.getWeatherFromCity(o.getCity(),o.getUnits());
            o.updateWeather(weather);
        }
    }

    /**
     * Starts weather thread.
     */
    public void start() {
        thread = new Thread(this, "Weather Thread");
        thread.start();
    }

    /**
     * Stops weather thread.
     */
    public void stop(){
        isRunning = false;
        thread.interrupt();
    }

    /**
     * Pause weather thread.
     */
    public void pause() {
        isRunning = false;
    }

    /**
     * Resume pasued thread.
     */
    public void resume(){
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * This method is calling when thread starts.
     */
    @Override
    public void run() {
        isRunning = true;

        while (isRunning) {
            try {
                updateObservers();
                updatedObserver = true;
                Thread.sleep(interval);
                updatedObserver = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            } catch (IOException e) {
                Controller.threadError("Wrong city entered or no internet connection",
                        "Check if there is no typo in city name or check your internet connection");
                return;

            }
        }

    }

}
