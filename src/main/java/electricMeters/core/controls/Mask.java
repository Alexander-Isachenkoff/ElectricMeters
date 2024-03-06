package electricMeters.core.controls;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mask {
    INTEGER("\\d*"),
    REAL("\\d*\\.?\\d*");

    private final String pattern;

    boolean matches(String string) {
        return string.matches(pattern);
    }
}
