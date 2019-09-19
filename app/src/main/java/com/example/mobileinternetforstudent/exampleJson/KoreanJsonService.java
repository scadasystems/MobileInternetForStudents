package com.example.mobileinternetforstudent.exampleJson;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

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
 * Date : 2019-07-18
 * Time : 23:39                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public interface KoreanJsonService {
    @GET("posts")   // https://koreanjson.com/posts 주소에 있는 posts 이다.
    Call<List<Post>> listPosts();
}
