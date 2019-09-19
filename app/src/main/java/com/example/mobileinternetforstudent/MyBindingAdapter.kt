package com.example.mobileinternetforstudent

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

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
 * Date : 2019-09-19
 * Time : 19:48
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 *********************************************************/
@BindingAdapter("imageUrl")
fun imageUrl(view: ImageView, uri: String) {
    Glide.with(view)
            .load(uri)
            .placeholder(R.mipmap.ic_launcher)
            .into(view)
}