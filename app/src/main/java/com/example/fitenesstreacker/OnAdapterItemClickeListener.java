package com.example.fitenesstreacker;

public interface OnAdapterItemClickeListener {

        void onClick(int id, String type);
        void onLongClick(int position, String type, int id);
}
