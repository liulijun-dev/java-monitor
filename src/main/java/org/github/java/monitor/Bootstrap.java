package org.github.java.monitor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bootstrap {
    private static final Bootstrap INSTANCE = new Bootstrap();

    public static Bootstrap getInstance() {
        return INSTANCE;
    }


}
