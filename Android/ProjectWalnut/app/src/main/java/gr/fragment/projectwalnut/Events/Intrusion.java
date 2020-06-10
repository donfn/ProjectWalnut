package gr.fragment.projectwalnut.Events;

public class Intrusion extends Event{

    String media;

    public Intrusion(String image, String title, String time, String media) {
        super(image, title, time);
        this.media = media;
    }
}
