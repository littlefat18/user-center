package com.dzl.usercenter.other;

import org.junit.jupiter.api.Test;

/**
 * @author dzl
 * @date 2022/8/4 15:07
 */
public class Test01 {

    @Test
    public void Test02(){

        String kfc_vme_50 = "";
        char[] chars = kfc_vme_50.toCharArray();
        for (Character char01: chars) {
            int i = ((int) char01);
            String j = Integer.toHexString(i);
            System.out.print("\\u"+j);
        }
//        int decimal = ((int)'');

//        System.out.println(decimal); // Unicode十进制编码

//        String hex = Integer.toHexString(decimal);
//
//        System.out.print("/u"+hex); // Unicode十六进制编码
    }
}
