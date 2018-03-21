<?php namespace App\Http\Controllers;

use App\user;
use App;

class UserController extends Controller {

	/**
	 * Create a new controller instance.
	 *
	 * @return void
	 */
      public function __construct()
      {
          $this->middleware('guest');
      }

	/**
	 * Returns the categories
     * @param $id
	 * @return Illuminate\View\view
	 */
    public function category($id)
    {
        return view('api/category');
    }

    /**
     * Returns values used in the app drawer menu
     * @param $id
     * @return \Illuminate\View\View
     */
    public function drawer($id)
    {
        return view('api/drawer');
    }

    /**
     * Returns the friend details
     * @param $id
     * @return \Illuminate\View\View
     */
    public function friends($id)
    {
        return view('api/friends');
    }

    /**
     * Returns the Ping invite list for the user
     * @param $id
     * @return \Illuminate\View\view
     */
    public function invite($id)
    {
        return view('api/invite');
    }

    /**
     * Returns the list of Pings
     * @param $id
     * @return \Illuminate\View\View
     */
    public function pings($id)
    {
        return view('api/pings');
    }

    /**
     * Returns the Ping detail
     * @param $id
     * @return \Illuminate\View\View
     */
    public function ping($id)
    {

        /**
         * This is how we CREATE

        $user = new user();
        $user->name = "John Doe";
        $user->status = "This is another status";
        $user->badge = "";
        $user->is_facebook = false;
        $user->is_twitter = false;
        $user->social_id = 0;
        $user->save();

        $user::create(['name'=>'Benjamin du Plessis', 'status'=>'This is a status', 'badge'=>'', 'is_facebook'=>false, 'is_twitter'=>false, 'social_id'=>0]);

        */

        /**
         * This is how we UPDATE

        $user = App\user::find(2);
        $user->name = "Joseph";
        $user->save();
        echo $user->name."<br/>";

        $userLand = App\user::find(2);
        $userLand->update(['name'=>'John Wick']);
        echo $userLand->name."<br/>";

        */


        /**
         * This is how we GET
         *
         * return App\user::where('name', 'like', 'Jo%')->get(); ; // returns all of jason without the template
         *
         */

        $user = new user();
        $pings = $user::where('name', 'like', 'Jo%')->get();

        return view('api/ping', compact('pings')); // compact actually convert a Collection to an array to use in Blade? in blade it's an object

    }

}
