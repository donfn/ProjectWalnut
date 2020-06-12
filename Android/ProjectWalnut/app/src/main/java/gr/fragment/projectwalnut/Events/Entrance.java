package gr.fragment.projectwalnut.Events;

public class Entrance extends Event {

    String person_id;

    public Entrance(String image, String title, String time, String person_id) {
        super(image, title, time);
        this.person_id = person_id;
    }
}
