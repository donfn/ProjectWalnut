package gr.fragment.projectwalnut.Events;

public abstract class Event {
    public String image, title, time;

    public Event(String image, String title, String time){
        this.image = image;
        this.title = title;
        this.time = time;
    }
}
