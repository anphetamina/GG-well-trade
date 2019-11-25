package steam;

import main.Trade;
import service.IFinder;

import java.util.ArrayList;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */
public class Steam implements IFinder {
    @Override
    public ArrayList<Trade> getTrades() {
        return null;
    }

    @Override
    public boolean updateItems() {
        return false;
    }
}
