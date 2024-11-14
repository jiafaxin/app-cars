package com.autohome.app.cars.mapper.popauto.providers;

import java.util.List;

public class BrandProvider {

    public String getBrand(int brandId) {
        String sql = "" +
                "SELECT A.*, B.BrandDescription AS description\n" +
                "FROM [group] AS A WITH(NOLOCK) \n" +
                "LEFT JOIN AppBrandInfo AS B WITH(NOLOCK) ON A.id = B.BrandId\n";

        if (brandId > 0) {
            sql += "WHERE A.id = #{brandId}";
        }
        return sql;
    }

    public String getAllBrands() {
        return getBrand(0);
    }

    public String getBrandList(List<Integer> seriesIds) {
        String sql = getAllBrands();
        sql += " WHERE A.id in (<foreach collection=\"seriesIds\" item=\"__item\" separator=\",\" >#{__item}</foreach>)";
        return "<script>"+sql+"</script>";
    }

}
