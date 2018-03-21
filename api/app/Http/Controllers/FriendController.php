<?php
/**
 * Created by PhpStorm.
 * User: joduplessis
 * Date: 2015/07/20
 * Time: 9:39 AM
 */

namespace App\Http\Controllers;

use App\user;
use Illuminate\Support\Facades\Input;

class FriendController extends Controller {

    public function getlist($id)
    {
        // WE DON'T USE THE ID FOR ANYTHING
        // THE LIST OF FRIEND ID (data GET)
        // IS RETRIEVED VIA THE APP FACEBOOK API

        // template data array to feed Blade
        $templateArray = [];

        // If it's not empty
        if (Input::get('data')!='') {

            // Eloquent data models for us to use
            $user = new user();

            // split them by the comma
            $explodedSocialIdArray = explode(",",Input::get('data'));

            // Get each one's data using their Facebook IDs
            // I've added the IF statement because with testing it's possible for the
            // use to be deleted from OUR DATABASE, but still authorized on FB
            foreach ($explodedSocialIdArray as $singleFacebookId) {
                if ($user::where('social_id', $singleFacebookId)->count()>0) {

                    // Get user object in the DB
                    $userDataObject = $user::where('social_id', $singleFacebookId)->get()->first();

                    // Store our values in an array to feed the template
                    array_push($templateArray, [
                        'id' => $userDataObject->id,
                        'facebookid' => $singleFacebookId,
                        'name' => $userDataObject->name,
                        'status' => $userDataObject->status,
                        'badge' => $userDataObject->badge,
                    ]);
                }
            }
        }

        // Give our friend data to the view
        return $templateArray; // view('api/friend/getlist', compact('templateArray'));
    }

}