package studio.greeks.books.crawler;

import studio.greeks.books.entity.Index;

import java.util.List;
import java.util.concurrent.Callable;

public interface NavGetter extends Callable<List<Index>> {
}
