package studio.greeks.books.crawler;

import org.jsoup.nodes.Element;
import studio.greeks.books.entity.Index;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class AbstractNavGetter implements Callable<List<Index>> {
    protected Element navElement;

    protected AbstractNavGetter(Element navElement) {
        this.navElement = navElement;
    }

    @Override
    public List<Index> call() throws Exception {
        return doGet();
    }

    protected abstract List<Index> doGet();
}
