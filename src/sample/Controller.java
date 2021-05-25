package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    @FXML
    public Button btnadd;
    public Button btnPrev;
    public Button btnPlay;
    public Button btnNext;
    public ListView<File> listview;
    public Label songTitle;
    public File selectedSong;
    public File selectedVideo;
    public MediaPlayer player;
    public MediaPlayer videoPlayer;
    public MediaView mediaView;
    public String songName;
    public Slider volumeSlider;
    public Slider progressBar;
    public double volume;
    public String workingDirectory = System.getProperty("user.dir");
    public String requiredDirectory = workingDirectory + "\\" + "src\\sample\\songs\\";

    @FXML
    public void initialize() {
        listview.getItems().clear();

        File file = new File(requiredDirectory);
        File[] files = file.listFiles();

        for(File f: files){
            listview.getItems().add(f);
        }

        listview.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
            public ListCell<File> call(ListView<File> param) {
                return new ListCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null || empty ? null : item.getName());
                    }
                };
            }
        });
    }

    @FXML
    public void handlebuttonAdd(ActionEvent actionEvent) throws IOException {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter;
        filter = new FileChooser.ExtensionFilter("MP3-File", "*.mp3");
        fc.getExtensionFilters().add(filter);
        File file = fc.showOpenDialog(null);
        songName = file.getName();
        if (file != null) listview.getItems().add(file);

        try {
            Path source = Paths.get(String.valueOf(file));
            Path dest = Paths.get(requiredDirectory + songName);
            Files.move(source, dest);
        } catch (IOException e){
            System.out.println(e);
        }
        initialize();
    }

    @FXML
    public void handleButtonSelect(MouseEvent actionEvent) {
        updateSong();
    }

    @FXML
    public void handleButtonPlay(ActionEvent actionEvent) {
        MediaPlayer.Status currentStatus = player.getStatus();

        if (currentStatus == MediaPlayer.Status.PLAYING) {
            player.pause();
            videoPlayer.pause();
            btnPlay.setText("Play");
        } else {
            player.play();
            videoPlayer.play();
            videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            btnPlay.setText("Pause");
        }

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                progressBar.setValue(newValue.toSeconds());
            }
        }
        );

    }

    @FXML
    public void handleButtonNext(ActionEvent actionEvent) {
        listview.getSelectionModel().selectNext();
        updateSong();
    }

    @FXML
    public void handleButtonPrev(ActionEvent actionEvent) {
        listview.getSelectionModel().selectPrevious();
        updateSong();
    }

    @FXML
    public void handleSliderVolume(Event actionEvent) {
        volumeSlider.setValue(player.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                volume = volumeSlider.getValue() / 100;
                player.setVolume(volume);
            }
        });
    }

    @FXML
    public void handleProgressPressed(Event actionEvent) {
        player.seek(Duration.seconds(progressBar.getValue()));
    }

    @FXML
    public void handleProgressDragged(Event actionEvent) {
        player.seek(Duration.seconds(progressBar.getValue()));
    }

    public void updateSong(){
        selectedSong = listview.getSelectionModel().getSelectedItem();
        songTitle.setText(selectedSong.getName());
        Media media = new Media(selectedSong.toURI().toString());
        player = new MediaPlayer(media);

        selectedVideo = new File("D:\\projects\\JavaProjects\\MusicPlayer\\src\\sample\\video\\video.mp4");
        Media mediaV = new Media(selectedVideo.toURI().toString());
        videoPlayer = new MediaPlayer(mediaV);
        mediaView.setMediaPlayer(videoPlayer);

        player.setVolume(volume);

        btnPlay.setText("Play");
        player.stop();
        videoPlayer.stop();

        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                javafx.util.Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
            }
        });
    }

}
