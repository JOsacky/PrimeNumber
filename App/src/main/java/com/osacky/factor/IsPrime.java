package com.osacky.factor;
import android.util.Log;

import java.util.*;

/**
 * Created by Jonathan on 12/6/13.
 */
public class IsPrime {

    public static List isPrime(int num, int lo, int hi)
    {
        List toRet = new ArrayList();

        if(lo==2 && num%2==0)
        {
            toRet.add(2);
            toRet.add(num>>1);
            Log.e("prime number:", "2");
            Log.e("prime number:", ""+ (num/2));

        }

        if(lo%2==0)
            lo = lo+1;

        for(int i=lo; i<=hi; i+=2)
        {
            if(num%i == 0)
            {
                toRet.add(i);
                Log.e("prime number:",""+ i);
                toRet.add(num/i);
                Log.e("prime number:",""+ (num/i));

            }
        }
        return toRet;
    }

}
