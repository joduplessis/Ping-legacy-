<?php
/**
 * Created by PhpStorm.
 * User: joduplessis
 * Date: 2015/07/20
 * Time: 9:39 AM
 */

namespace App\Http\Controllers;

Use App\user;
Use Illuminate\Support\Facades\Input;

class ProfileController extends Controller {

    public function get($id)
    {
        // Array to feed our template
        $templateArray = [];

        // Eloquent data models for us to use
        $user = new user();

        // If it exists
        if ($user::where('social_id', $id)->count()>0) {

            // Get user object in the DB
            $userDataObject = $user::where('social_id', $id)->get()->first();

            // Store our values in an array to feed the template
            array_push($templateArray, [
                'id' => $userDataObject->id,
                'facebookid' => $userDataObject->social_id,
                'name' => $userDataObject->name,
                'status' => $userDataObject->status,
                'badge' => $userDataObject->badge,
                'token' => $userDataObject->token
            ]);

        }

        // Return JSON
        return $templateArray; // view('api/profile/get', compact($id));

    }

    public function create($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Create a new user
        $user = new user();
        $user->name = Input::get('name');
        $user->status = "I really need to update my status";
        $user->badge = "badge_sock";
        $user->social_id = $id;
        $user->token = $id."".substr(time(), 8, 10);
        $user->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'id'=>$user->id
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/profile/create', compact($id));
    }

    public function update($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Update
        $userEloquentModel = new user();
        $user = $userEloquentModel::where('social_id', $id)->get()->first();
        $user->name = Input::get('name');
        $user->status = Input::get('status');
        $user->badge = Input::get('badge');
        $user->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/profile/update', compact($id));
    }

}