package dev.conorthedev.mediamod.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A util for scheduling tick-tasks
 */
public class TickScheduler {

    /**
     * Instance of this scheduler
     */
    public static final TickScheduler INSTANCE = new TickScheduler();

    /**
     * List of tasks to execute
     */
    private final List<Task> items = new ArrayList<>();

    private TickScheduler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Schedules a task that executes after a specific amount of ticks
     *
     * @param ticksToWait Ticks to wait
     * @param runnable    Task to execute
     */
    public void schedule(int ticksToWait, Runnable runnable) {
        items.add(new Task(ticksToWait, runnable));
    }

    @SubscribeEvent
    public void onTickClientTick(ClientTickEvent event) {
        items.removeIf(Task::execute);
    }

    /**
     * A simple task container
     */
    private static class Task {

        /**
         * Task to execute
         */
        private final Runnable task;
        /**
         * Ticks left until the task executes
         */
        private int ticksLeft;

        private Task(int ticksLeft, Runnable runnable) {
            this.ticksLeft = ticksLeft;
            this.task = runnable;
        }

        /**
         * Decrements the ticks left, and if it's 0 the task is ran and removed from the queue.
         *
         * @return True if the task should run and get removed
         */
        boolean execute() {
            if (ticksLeft <= 0) {
                task.run();
                return true;
            }
            ticksLeft--;
            return false;
        }
    }
}