package com.example.stickheadersortrecyclerview.bean;

/**
 * Author: 李桐桐
 * Date: 2019-11-21
 * Description:
 */
public class ItemBean {

        private int picRsId;
        private String name;
        private String letters;

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public int getPicRsId() {
            return picRsId;
        }

        public void setPicRsId(int picRsId) {
            this.picRsId = picRsId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ItemBean(int picRsId, String name) {
            this.picRsId = picRsId;
            this.name = name;
        }
}

