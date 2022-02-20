/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import javafx.scene.media.AudioClip;

/**
 *
 * @author user1
 */
public class Sound {
    String url;
    AudioClip clip;
    public Sound(){
        this.url = getClass().getResource("/juma/resources/alarm.wav").toExternalForm();
        clip = new AudioClip(url);
        clip.setVolume(1.0);
    }
    public void stop(){
        if(clip.isPlaying()){
            clip.stop();
        }
        clip.stop();
    }
    public void play(){
        clip.play();
    }
}
