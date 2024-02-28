package org.apache.bigtop.manager.spi.plugin;

public interface PrioritySPI extends Comparable<Integer> {

    /**
     * The SPI identify, if the two plugin has the same name, will load the high priority.
     * If the priority and name is all same, will throw <code>IllegalArgumentException</code>
     *
     * @return SPI Name
     */
    default String getName() {
        return this.getClass().getName();
    }

    default Integer getPriority() {
        return 0;
    }

    @Override
    default int compareTo(Integer o) {
        return Integer.compare(getPriority(), o);
    }

}
