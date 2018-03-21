<?php

/**
 * These are the default routes that I'm keep as backup
 * They outline basic usage and might be useful to use as reference
 */
Route::get('/', 'WelcomeController@index');
Route::get('home', 'HomeController@index');

Route::get('user/{id}/category', 'UserController@category') ;
Route::get('user/{id}/drawer', 'UserController@drawer') ;
Route::get('user/{id}/friends', 'UserController@friends') ;
Route::get('user/{id}/invite', 'UserController@invite') ;
Route::get('user/{id}/pings', 'UserController@pings') ;
Route::get('user/{id}/ping', 'UserController@ping') ;

Route::get('login', 'LoginController@login') ;
Route::post('login/submit', 'LoginController@submit') ;

/**
 * These are the new routes for the API
 */

Route::get('category/{id}/getlist', 'CategoryController@getlist');
Route::get('category/{id}/create', 'CategoryController@create');
Route::get('category/{id}/delete', 'CategoryController@delete');
Route::get('category/{id}/update', 'CategoryController@update');

Route::get('ping/{id}/get', 'PingController@get');
Route::get('ping/{id}/get_next_ping', 'PingController@getNextPing');
Route::get('ping/{id}/getlist', 'PingController@getlist');
Route::get('ping/{id}/getcount', 'PingController@getcount');
Route::get('ping/{id}/create', 'PingController@create');
Route::get('ping/{id}/delete', 'PingController@delete');
Route::get('ping/{id}/update', 'PingController@update');
Route::get('ping/{id}/update_category', 'PingController@updateCategory');
Route::get('ping/{id}/update_sound', 'PingController@updateSound');
Route::get('ping/{id}/disable', 'PingController@disable');
Route::get('ping/{id}/friends', 'PingController@friends');

Route::get('invite/{id}/getlist', 'InviteController@getlist');
Route::get('invite/{id}/accept', 'InviteController@accept');
Route::get('invite/{id}/reject', 'InviteController@reject');
Route::get('invite/{id}/create', 'InviteController@create');
Route::get('invite/{id}/delete', 'InviteController@delete');
Route::get('invite/{id}/get', 'InviteController@get');

Route::get('profile/{id}/get', 'ProfileController@get');
Route::get('profile/{id}/update', 'ProfileController@update');
Route::get('profile/{id}/create', 'ProfileController@create');

Route::get('friend/{id}/getlist', 'FriendController@getlist');

Route::get('drawer/{id}/get', 'DrawerController@get');

/**
 * Laravel boilerplate controller
 * Not sure to delete it just yet
 */
Route::controllers([
	'auth' => 'Auth\AuthController',
	'password' => 'Auth\PasswordController',
]);