package gr.fragment.projectwalnut.Events;

public class Alarm  extends Event {

    Boolean armed;
    String person_id;

    public Alarm(String image, String title, String time, Boolean armed, String person_id) {
        super(image, title, time);
        this.armed = armed;
        this.person_id = person_id;
    }
}
