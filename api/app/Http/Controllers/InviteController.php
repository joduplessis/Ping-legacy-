<?php
/**
 * Created by PhpStorm.
 * User: joduplessis
 * Date: 2015/07/20
 * Time: 9:39 AM
 */

namespace App\Http\Controllers;

Use App\user;
Use App\pinguser;
Use App\ping;
Use Illuminate\Support\Facades\Input;

class InviteController extends Controller {

    public function getlist($id)
    {
        // NOT the Facebook ID

        // template data array to feed Blade
        $templateArray = [];

        // Set up our Eloquent object
        $user = new user();
        $ping = new ping();
        $pinguser = new pinguser();

        // We take the id from the database ID
        $pinguserObjects = $pinguser::where('user_id', $id)->where('approved', false)->get();

        // Loop through each one
        foreach ($pinguserObjects as $pinguserObject) {

            // Friend name & badge (owner of the Ping)
            $pingId = $pinguserObject->ping_id;

            // Get the ping
            $pingObject = $ping::where('id', $pingId)->get()->first();

            // Get the user id
            $pingOwnerId = $pingObject->user_id;
            $pingTitle = $pingObject->title;

            // Get the user from this ID
            $pingOwnerUserObject = $user::where('id', $pingOwnerId)->get()->first();

            // create variables
            $pingOwnerName = $pingOwnerUserObject->name;
            $pingOwnerBadge = $pingOwnerUserObject->badge;
            $pinguserId = $pinguserObject->id;

            // Store our values in an array to feed the template
            array_push($templateArray,[
                'id'=>$pinguserId,
                'title'=>$pingTitle,
                'friend_username'=>$pingOwnerName,
                'friend_badge'=>$pingOwnerBadge
            ]);
        }

        return $templateArray; //  return view('api/invite/getlist', compact($id));
    }

    public function accept($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        $pinguser = pinguser::find($id);
        $pinguser->approved = true;
        $pinguser->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/invite/accept', compact($id));
    }

    public function reject($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        $pinguser = pinguser::find($id);
        $pinguser->approved = false;
        $pinguser->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/invite/reject', compact($id));
    }

    public function create($id)
    {

        // PING ID IS USED HERE

        // template data array to feed Blade
        $templateArray = [];

        $pinguser = new pinguser();
        $pinguser->ping_id = $id;
        $pinguser->user_id = Input::get('friend');
        $pinguser->approved = false;
        $pinguser->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>$pinguser->id
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/invite/create', compact($id));
    }

    public function delete($id)
    {

        // PING ID IS USED HERE

        // template data array to feed Blade
        $templateArray = [];

        // Create a new user
        $pinguser = pinguser::find($id);
        $pinguser->delete();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/invite/create', compact($id));
    }

    public function get($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // PING ID
        // Set up our Eloquent object
        $pinguser = new pinguser();


        // We take the id from the database ID
        // NOT the Facebook ID

        if ($pinguser::where('user_id', Input::get('friend'))->where('ping_id', $id)->count()>0) {
            $pinguserObject = $pinguser::where('user_id', Input::get('friend'))->where('ping_id', $id)->get()->first();
            // Store our values in an array to feed the template
            array_push($templateArray,[
                'id'=>$pinguserObject->id,
                'ping_id'=>$pinguserObject->ping_id,
                'user_id'=>$pinguserObject->user_id,
                'approved'=>$pinguserObject->approved
            ]);
        }




        return $templateArray; // view('api/category/getlist', compact($id));
    }

}