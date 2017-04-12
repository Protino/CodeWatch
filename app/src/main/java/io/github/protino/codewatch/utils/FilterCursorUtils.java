package io.github.protino.codewatch.utils;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.List;

/**
 * Created by Gurupad Mamadapur on 16-03-2017.
 */

public class FilterCursorUtils extends CursorWrapper {

    private final List<Integer> filterList;
    private int currentPosition;

    public FilterCursorUtils(Cursor cursor, List<Integer> filterList) {
        super(cursor);
        this.filterList = filterList;
    }

    @Override
    public int getCount() {
        return filterList.size();
    }

    @Override
    public boolean moveToPosition(int position) {
        //Get original cursor size
        int count = getCount();
        if (position >= count) {
            currentPosition = count;
            return false;
        }

        if (position < 0) {
            currentPosition = 0;
        }
        try {
            currentPosition = filterList.get(position);
            return super.moveToPosition(currentPosition);
        } catch (IndexOutOfBoundsException e) {
            currentPosition = position;
            return true;
        }
    }

    @Override
    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public boolean moveToNext() {
        return moveToPosition(currentPosition + 1);
    }

    @Override
    public boolean moveToPrevious() {
        return moveToPosition(currentPosition - 1);
    }

    @Override
    public boolean isFirst() {
        return currentPosition == 0 && getCount() != 0;
    }

    @Override
    public boolean isLast() {
        int count = getCount();
        return currentPosition == (count - 1) && count != 0;
    }

    @Override
    public boolean isBeforeFirst() {
        return getCount() == 0 || currentPosition == -1;
    }

    @Override
    public boolean isAfterLast() {
        int count = getCount();
        return count == 0 || currentPosition == count;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    public boolean move(int offset) {
        return moveToPosition(currentPosition + offset);
    }
}
