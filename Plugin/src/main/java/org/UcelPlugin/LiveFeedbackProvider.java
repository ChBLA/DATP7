package org.UcelPlugin;

import org.UcelParser.Compiler;
import org.UcelParser.Util.Exception.ErrorsFoundException;

public class LiveFeedbackProvider {

    private boolean enabled;
    private UppaalManager uppaalManager;

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enable) {
        enabled = enable;
        if(!enabled)
            stopFeedbackThread();
    }

    public LiveFeedbackProvider(UppaalManager manager, boolean enabled) {
        uppaalManager = manager;
        this.enabled = enabled;

        // Live updating error list
        uppaalManager.addOnDocChange((updateType) -> {
            if(this.enabled)
                runFeedbackAsync();
        });
    }


    FeedbackThread feedbackThread;
    protected void stopFeedbackThread() {
        if(feedbackThread != null) {
            feedbackThread.kill();
        }
    }

    public void runFeedbackAsync() {
        stopFeedbackThread();
        feedbackThread = new FeedbackThread();
        feedbackThread.start();
    }



    private class FeedbackThread {
        private Thread thread;
        private boolean killed;

        public FeedbackThread() {
            thread = new Thread(() -> {
                runFeedback();
            });
            killed = false;
        }

        public void start() {
            thread.start();
        }

        public void kill() {
            killed = true;

            if(!thread.isInterrupted())
                thread.interrupt();
        }

        private void runFeedback() {
            Compiler compiler = new Compiler();
            try {
                compiler.checkProject(uppaalManager.getProject());
                uppaalManager.clearProblems();
            }
            catch (ErrorsFoundException e) {
                try {
                    if (!killed) {
                        uppaalManager.clearProblems();
                        for (var log : e.getLogs()) {
                            uppaalManager.addProblem("location", log.getFancyMessage());
                        }
                        uppaalManager.updateProblemDisplay();
                    }
                }
                catch (Throwable ignored) {}
            }
            catch (Throwable err) {
                try {
                    if (!killed) {
                        uppaalManager.clearProblems();
                        uppaalManager.addProblem("location", err.getMessage());
                        uppaalManager.updateProblemDisplay();
                    }
                }
                catch (Throwable ignored) {}
            }
        }
    }

}
