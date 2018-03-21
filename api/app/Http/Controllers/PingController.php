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
use Illuminate\Support\Facades\Input;

class PingController extends Controller {

    public function getNextPing($id)
    {

        // template data array to feed Blade later on
        $templateArray = [];

        // Set up our Eloquent model
        $ping = new ping();
        $pinguser = new pinguser();

        $dayArray = ["","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"];

        $dayNow = $dayArray[Input::get('day')];
        $hourNow = Input::get('hour');
        $minuteNow = Input::get('minute');

        // Get all our pings
        $pingObjects = $ping::where('enabled', true)->where('user_id', $id)->get();
        $pinguserObjects = $pinguser::where('approved', true)->where('user_id', $id)->get();
        $allPingObjects = [];
        $foundNextPing = false;

        // Find all pings
        foreach ($pingObjects as $pingObject) {
            if (strpos($pingObject->days, $dayNow)!==false) {
                array_push($allPingObjects, $pingObject);
            }
        }

        // Find all invites - and then find the ping for each
        foreach ($pinguserObjects as $pinguserObject) {
            $pinguserObjectFind =  $ping::where('id', $pinguserObject->id)->get()-first();
            if (strpos($pinguserObjectFind->days, $dayNow)!==false) {
                array_push($allPingObjects, $pinguserObjectFind);
            }
        }

        // Loop all the remaining hours for today
        for ($h=$hourNow; $h<24; $h++) {
            if (!$foundNextPing) {

                $hour = $h;

                // Loop all the remaining minutes
                for ($m = 0; $m < 60; $m++) {

                    $minute = $m;

                    // We only do this for the first day, so that we don't start on 0
                    if ($hour == $hourNow) $minute += $minuteNow + 1;

                    // Loop through our array of ping objects
                    foreach ($allPingObjects as $allPingObject) {

                        $ptime = explode(" ", $allPingObject["time"])[1];
                        $phour = explode(":", $ptime)[0];
                        $pminutes = explode(":", $ptime)[1];

                        if ($pminutes == $minute && $phour == $hour) {
                            array_push($templateArray,[
                                'id'=>$allPingObject["id"],
                                'title'=> $allPingObject["title"],
                                'time'=> $allPingObject["time"],
                                'sound'=> $allPingObject["sound"],
                                'fadein'=> $allPingObject["fadein"],
                            ]);
                            $foundNextPing = true;
                        }

                    }


                    // If we go over 60 ONLY FOR THE FIRST day, then exit
                    if ($minute >= 59) break;
                }

            }
        }

        // Get the JSON
        return $templateArray; // view('api/ping/get', compact($id));
    }

    public function get($id)
    {

        // template data array to feed Blade later on
        $templateArray = [];

        // Store for our badges, we instantiate it here because we need to re-use it
        $badgeList = [];

        // Set up our Eloquent model
        $user = new user();
        $ping = new ping();
        $pinguser = new pinguser();
        $category = new category();

        // Get the main ping details
        $singlePingObject = $ping::where('id', $id)->get()->first();

        // We need the name of the creator
        $getSingleUserObject = $user::where('id', $singlePingObject->user_id)->get()->first();

        // Add this users badge first
        array_push($badgeList, $getSingleUserObject->badge);

        // Now we need the category
        $getSingleCategoryObject = $category::where('id', $singlePingObject->category)->get()->first();
        $getSingleCategoryObjectName = "";
        $getSingleCategoryObjectId = "";

        if ($category::where('id', $singlePingObject->category)->get()->count()==0) {
            $getSingleCategoryObjectName = "";
            $getSingleCategoryObjectId = "";
        } else {
            $getSingleCategoryObjectName = $getSingleCategoryObject->title;
            $getSingleCategoryObjectId = $getSingleCategoryObject->id;
        }

        // Now we need to go get all other badges
        $getFriendsOnThisPing = $pinguser::where('ping_id', $id)->where('approved', true)->get();

        // Iterate over all of them for their badges (we're cheap like that)
        foreach($getFriendsOnThisPing as $getSingleFriendObject) {

            // Query Eloquent using the friend id
            $singleFriend = $user::where('id', $getSingleFriendObject->user_id)->get()->first();

            // Push it to our badge array
            array_push($badgeList, $singleFriend->badge);
        }

        // Number of people on this ping
        $userCountOnPing = $pinguser::where('ping_id', $id)->count();
        $userCountOnPing++;

        // Make a string out of an array
        $badgeListExploded = join(",", $badgeList);

        array_push($templateArray,[
            'id'=>$id,
            'title'=> $singlePingObject->title,
            'createdby'=> $getSingleUserObject->name,
            'createdbybadge'=> $getSingleUserObject->badge,
            'usercount'=> $userCountOnPing,
            'time'=> $singlePingObject->time,
            'repeatsweekly'=> $singlePingObject->repeat,
            'repeatdays'=> $singlePingObject->days,
            'categoryid'=> $getSingleCategoryObjectId,
            'category'=> $getSingleCategoryObjectName,
            'sound'=> $singlePingObject->sound,
            'fadein'=> $singlePingObject->fadein,
            'enabled'=> $singlePingObject->enabled,
            'badges'=> $badgeListExploded
        ]);

        // Get the JSON
        return $templateArray; // view('api/ping/get', compact($id));
    }

    public function getlist($id)
    {

        // template data array to feed Blade later on
        $templateArray = [];

        // Set up our Eloquent model
        $user = new user();
        $ping = new ping();
        $pinguser = new pinguser();

        // First we need to find the pings that the user created
        // -----------------------------------------------------
        // -----------------------------------------------------
        // -----------------------------------------------------

        // Query the PING data model
        $pings = $ping::where('user_id', $id)->get();

        // Loop through each ping returned from Eloquent
        foreach ($pings as $pingObject) {

            // Store for our badges, we instantiate it here because we need to re-use it
            $badgeList = [];

            // Find the user object associated with this id (for other details)
            $userObject = $user::where('id', $id)->get()->first();

            // Add this users badge first
            array_push($badgeList, $userObject->badge);

            // Now we need to find all the other users (friends) associated with this ping
            $pinguserObjects = $pinguser::where('ping_id', $pingObject->id)->where('approved', true)->get();

            // Iterate over the returned pinguserObjects (friends)
            foreach ($pinguserObjects as $pinguserObject) {

                // Take their ID & query Eloquent to find their badge
                $pinguserObjectFirst = $user::where('id', $pinguserObject->user_id)->get()->first();

                // Push their badge onto our array above
                array_push($badgeList, $pinguserObjectFirst->badge);
            }

            // Convert the badge array into a comma delimited string
            $badgeListExploded = join(",", $badgeList);

            // Push this ping onto the template data array
            array_push($templateArray,[
                'id'=>$pingObject->id,
                'title'=>$pingObject->title,
                'time'=>$pingObject->time,
                'repeatsweekly'=>$pingObject->repeat,
                'repeatdays'=>$pingObject->days,
                'createdby'=>$userObject->name,
                'badges'=>$badgeListExploded,
                'enabled'=>$pingObject->enabled,
                'owner'=>true
            ]);

        }

        // We also need all of the pings that the user was added to from pinguser
        // ----------------------------------------------------------------------
        // ----------------------------------------------------------------------
        // ----------------------------------------------------------------------

        // Query the data model - get all of the pings this user is added to AS FRIEND
        $pinguserObjects = $pinguser::where('user_id', $id)->where('approved', true)->get();

        // Again, we iterate over all of them to get the details
        foreach ($pinguserObjects as $pinguserObject) {

            // Store for our badges, we instantiate it here because we need to re-use it
            $badgeList = [];

            // Call Eloquent to get the details about the original ping
            $originalPing = $ping::where('id', $pinguserObject->ping_id)->get()->first();

            // Now get the name of the original creator of the ping
            $originalPingOwner = $user::where('id',$originalPing->user_id)->get()->first();

            // Now, of course, we need all of the badges with this ping too
            // Get all of the user also added to this ping (id)
            $pinguserObjectsInner = $pinguser::where('ping_id', $originalPingOwner->id)->where('approved', true)->get();

            // Iterate over the returned pinguserObjects (friends)
            foreach ($pinguserObjectsInner as $pinguserObjectInner) {

                // Take their ID & query Eloquent to find their badge
                $pinguserObjectInnerUser = $user::where('id', $pinguserObjectInner->user_id)->get()->first();

                // Push their badge onto our array above
                array_push($badgeList,  $pinguserObjectInnerUser->badge);
            }

            // Convert the badge array into a comma delimited string
            $badgeListExploded = join(",", $badgeList);

            // Push this ping onto the template data array
            array_push($templateArray,[
                'id'=>$originalPing->id,
                'title'=>$originalPing->title,
                'time'=>$originalPing->time,
                'repeatsweekly'=>$originalPing->repeat,
                'repeatdays'=>$originalPing->days,
                'createdby'=>$originalPingOwner->name,
                'badges'=>$badgeListExploded,
                'owner'=>false
            ]);

        }

        // Give our friend data to the view
        return $templateArray; // view('api/ping/getlist', compact($id));
    }

    public function getcount($id)
    {

        // id = token

        // template data array to feed Blade later on
        $templateArray = [];

        // Set up our Eloquent model
        $user = new user();
        $ping = new ping();
        $pinguser = new pinguser();

        if ($userToken = $user::where('token', $id)->count()==0) {

            array_push($templateArray, [
                'success' => false
            ]);

        } else {

            $userToken = $user::where('token', $id)->get()->first();

            $uid = $userToken->id;

            // Query the PING data model
            $pingsCount = $ping::where('user_id', $uid)->count();

            // Query the data model - get all of the pings this user is added to AS FRIEND
            $pinguserObjects = $pinguser::where('user_id', $uid)->where('approved', true)->count();

            $total = $pingsCount + $pinguserObjects;

            array_push($templateArray, [
                'success' => true,
                'count' => $total,
                'user_id' => $uid
            ]);

        }

        // Give our friend data to the view
        return $templateArray; // view('api/ping/getlist', compact($id));
    }

    public function friends($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Eloquent data models for us to use
        $user = new user();
        $pinguser = new pinguser();

        // If it's not empty
        if (Input::get('data')!='') {

            // Now we have a list of the user's friends
            $explodedSocialIdArray = explode(",",Input::get('data'));

            // Get each one's data using their Facebook IDs
            foreach ($explodedSocialIdArray as $singleFacebookId) {

                // Get the FRIEND user object in the DB
                $userObject = $user::where('social_id', $singleFacebookId)->get()->first();

                // Get the user id
                $userObjectId = $userObject->id;

                // See if the FRIEND user is approved for this ping ($id)
                $isPingApproved = "no";
                $isThisPingApprovedForTheFriend = $pinguser::where('user_id', $userObjectId)->where('ping_id', $id)->where('approved', true)->count();
                if ($isThisPingApprovedForTheFriend>0)
                    $isPingApproved = "yes";

                // Store our values in an array to feed the template
                array_push($templateArray,[
                    'id'=>$userObjectId,
                    'facebookid'=>$userObject->social_id,
                    'name'=>$userObject->name,
                    'status'=>$userObject->status,
                    'badge'=>$userObject->badge,
                    'approved'=>$isPingApproved
                ]);

            }
        }

        // Give our friend data to the view
        return $templateArray; // view('api/ping/friends', compact($id));
    }

    public function create($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Create a new one
        // These are all of the get variables passed to the API
        $ping = new ping();
        $ping->user_id = Input::get('userid');
        $ping->title = Input::get('title');
        $ping->time = Input::get('time');
        $ping->days = Input::get('days');
        $ping->category = Input::get('category');
        $ping->repeat = Input::get('repeat');
        $ping->sound = Input::get('sound');
        $ping->enabled = Input::get('enabled');
        $ping->fadein = Input::get('fadein');
        $ping->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/ping/create', compact($id));
    }

    public function update($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Find and update
        // These are all of the get variables passed to the API
        $ping = ping::find($id);
        $ping->user_id = Input::get('userid');
        $ping->title = Input::get('title');
        $ping->time = Input::get('time');
        $ping->days = Input::get('days');
        $ping->category = Input::get('category');
        $ping->repeat = Input::get('repeat');
        $ping->sound = Input::get('sound');
        $ping->enabled = Input::get('enabled');
        $ping->fadein = Input::get('fadein');
        $ping->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/ping/update', compact($id));
    }

    public function delete($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Delete the ping
        $ping = ping::find($id);
        $ping->delete();

        // Delete the occurences for invites
        $pingusers = pinguser::where('ping_id', $id)->get();
        foreach ($pingusers as $pinguserObject) {
            $pinguserObject->delete();
        }

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/ping/delete', compact($id));
    }

    public function disable($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Find and update the sound
        $ping = ping::find($id);
        $ping->enabled = false;
        $ping->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/ping/update_sound', compact($id));
    }

    /*
     * These 2 are actually never called anymore, because I update things via "update"
     */

    public function updateCategory($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Find and update the category
        $ping = ping::find($id);
        $ping->category = Input::get('category');
        $ping->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/ping/update_category', compact($id));
    }

    public function updateSound($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Find and update the sound
        $ping = ping::find($id);
        $ping->sound = Input::get('sound');
        $ping->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/ping/update_sound', compact($id));
    }


}
