package com.autohome.app.cars.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用key value
 * @param <T1>
 * @param <T2>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueDto<T1,T2> {
    T1 key;
    T2 value;
}
