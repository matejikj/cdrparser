package configs;

public interface Header {
    String getCode();
    String getTranslation();

    static <T extends Enum<T> & Header> T getByCode(Class<T> enumClass, String code) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.getCode().equals(code)) {
                return constant;
            }
        }
        return null;
    }
}
