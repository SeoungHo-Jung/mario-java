package com.samj.mario.editor;

public interface LevelDecoder {
    Level decode(byte[] bytes);
}
