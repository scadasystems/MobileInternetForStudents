package com.example.mobileinternetforstudent

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

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
 * Time : 17:55
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 *********************************************************/

fun Context.toast(text: String) {
    Toast.makeText(this, "$text", Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(text:String) {
    Toast.makeText(requireContext(), "$text", Toast.LENGTH_SHORT).show()
}