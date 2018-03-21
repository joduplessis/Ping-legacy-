<?php
/**
 * Created by PhpStorm.
 * User: joduplessis
 * Date: 2015/07/20
 * Time: 9:39 AM
 */

namespace App\Http\Controllers;

use App\user;
use App\pinguser;
use App\category;
use App\ping;
use App;

class DrawerController extends Controller {

    public function get($id)
    {
        // ID IS THE USER ID - NOT THE FACEBOOK ID

        // Array to feed our template
        $templateArray = [];

        // Eloquent data models for us to use
        $user = new user();
        $category = new category();
        $ping = new ping();
        $pinguser = new pinguser();

        // Get user object in the DB
        $userDataObject = $user::where('id', $id)->get()->first();

        // Get other details
        $pingCount = $ping::where('user_id', $id)->count();
        $categoryCount = $category::where('user_id', $id)->count();
        $pingUserCount = $pinguser::where('user_id', $id)->where('approved', true)->count();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'pings'=>$pingCount,
            'categories'=>$categoryCount,
            'friends'=>0,
            'invites'=>$pingUserCount,
            'name'=>$userDataObject->name,
            'status'=>$userDataObject->status,
            'badge'=>$userDataObject->badge,
            'token'=>$userDataObject->token,
            'success'=>'yes'
          ]);

        // We don't return the template
        return $templateArray; // view('api/drawer/get', compact('templateArray'));
    }

}
