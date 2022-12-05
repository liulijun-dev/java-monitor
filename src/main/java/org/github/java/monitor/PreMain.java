package org.github.java.monitor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.github.java.monitor.trasnformer.ProfilingTransformer;

import java.lang.instrument.Instrumentation;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PreMain {

    public static void premain(String options, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ProfilingTransformer());
    }
}
