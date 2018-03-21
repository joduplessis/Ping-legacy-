<?php
/**
 * Created by PhpStorm.
 * User: joduplessis
 * Date: 2015/07/20
 * Time: 9:39 AM
 */

namespace App\Http\Controllers;

Use App\category;
Use App\ping;
Use Illuminate\Support\Facades\Input;

class CategoryController extends Controller {

    public function getlist($id)
    {
        // Set up our Eloquent object
        $category = new category();
        $ping = new ping();

        // We take the id from the database ID
        // NOT the Facebook ID
        $categoryObjects = $category::where('user_id', $id)->get();

        // template data array to feed Blade
        $templateArray = [];

        // Loop through each one
        foreach ($categoryObjects as $categoryObject) {
            // Get the number of pings with thi category
            $pingCount = $ping::where('category', $categoryObject->id)->count();

            // Store our values in an array to feed the template
            array_push($templateArray, [
                'id'=>$categoryObject->id,
                'title'=>$categoryObject->title,
                'pings'=>$pingCount
            ]);
        }

        return $templateArray; // view('api/category/getlist', compact($id));
    }

    public function create($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Create a new user
        $category = new category();
        $category->title = Input::get('title');
        $category->user_id = $id;
        $category->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/category/create', compact($id));
    }

    public function delete($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Create a new user
        $category = category::find($id);
        $category->delete();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/category/delete', compact($id));
    }

    public function update($id)
    {

        // template data array to feed Blade
        $templateArray = [];

        // Create a new user
        $category = category::find($id);
        $category->title = Input::get('title');
        $category->save();

        // Store our values in an array to feed the template
        array_push($templateArray,[
            'success'=>'yes'
        ]);

        // Return the array for JSON output
        return $templateArray; // view('api/category/update', compact($id));
    }

}
