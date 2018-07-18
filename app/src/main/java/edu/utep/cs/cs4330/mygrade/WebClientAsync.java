package edu.utep.cs.cs4330.mygrade;

import android.os.Handler;

/**
 * Find a student's grade using the grade web service available at:
 * <code>http://www.cs.utep.edu/cheon/cs4330/grade/index.php</code>.
 * The web service take a user name and a PIN as a query string, e.g.,
 * <code>?user=staff&pin=1234</code>, and return the requested grade,
 * or an error if no grade is found.
 *
 * The <code>query</code> method is invoked asynchronously; i.e., it
 * creates a new thread to perform a network operations.
 *
 * <pre>
 *     WebClient web = new WebClient(new GradeListener() {
 *         public void onGrade(String date, Grade grade) {
 *             ...
 *         }
 *         public void onError(String msg) {
 *             ...
 *         }
 *     });
 *     web.query("staff", "1234");
 * </pre>
 *
 * Note that the callback methods (onGrade and onError) are invoked
 * in the caller's thread.
 */
public class WebClientAsync extends WebClient {

    private ListenerWrapper listenerWrapper;

    /** Create a new instance. */
    public WebClientAsync(GradeListener listener) {
        super(listener = new ListenerWrapper(listener));
        listenerWrapper = (ListenerWrapper) listener;
    }

    /** Query a grade and notify it to the listener. */
    public void query(String user, String pin) {
        listenerWrapper.setHandler(new Handler());
        new Thread(() -> super.query(user, pin)).start();
    }

    private static class ListenerWrapper implements GradeListener {
        private GradeListener listener;
        private Handler handler;

        public ListenerWrapper(GradeListener listener) {
            this.listener = listener;
        }

        public void setHandler(Handler handler) {
            this.handler = handler;
        }

        public void onGrade(String date, Grade grade) {
            handler.post(() -> listener.onGrade(date, grade));
        }

        public void onError(String msg) {
            handler.post(() -> listener.onError(msg));
        }
    }
}
