<?php namespace App\Http\Controllers;

use Illuminate\Support\Facades\Input;

class LoginController extends Controller {

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
     * Default login controller
     * @return \Illuminate\View\View
     */
    public function login()
    {
        return view('api/login');
    }

    public function submit()
    {
        $test_data = ['Jo', 'Ben'] ;

        /**
         * Here we actually get the form variables submitted
         * We add them to the area
         */
        array_push($test_data, Input::get('username'));
        array_push($test_data, Input::get('password'));

        return view('api/submit', compact('test_data'));
    }


}