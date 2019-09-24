package com.example.mobileinternetforstudent;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/*********************************************************
 * $$ \                               $$ \                      $$\            $$\
 * $$ |                               $$ |                      $$$\        $$$ |
 * $$ |          $$\       $$ \  $$ |  $$$$$$$$\   $$$$\    $$$$ |
 * $$ |          $$ |      $$ |  $$ |  \_____ $$    |   $$ \$$\$$ $$ |
 * $$ |          $$ |      $$ |  $$ |     $$$$ __/     $$   \$$$  $$ |
 * $$ |          $$ |      $$ |  $$ |   $$  ___/        $$ |  \$    /$$ |
 * $$$$$$$$\\$$$$$$   |  $$ | $$$$$$$$\    $$ |   \__/  $$ |
 * \_____________|\_________/    \___|  \____________|    \__|           \__|
 *
 * Project : MobileInternetForStudent                             
 * Created by Android Studio                           
 * Developer : Lulz_M                                    
 * Date : 2019-09-24                                        
 * Time : 18:20                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class SquareImageView extends AppCompatImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
