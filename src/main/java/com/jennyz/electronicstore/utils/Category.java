package com.jennyz.electronicstore.utils;

import javax.persistence.criteria.CriteriaBuilder;

public enum Category {
    MOBILES(0),BOOKS(1),CLOTHS(2),FRUITS(3),VEGETABLES(4),OTHERS(5);

    public final Integer code;

    private Category(Integer code) {
        this.code = code;
    }
}
