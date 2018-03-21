<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreatePingsTable extends Migration {

	/**
	 * Run the migrations.
	 *
	 * @return void
	 */
	public function up()
	{
		Schema::create('pings', function(Blueprint $table)
		{
            $table->increments('id');
            $table->integer('user_id');
            $table->string('title');
            $table->timestamp('time');
            $table->string('days');
            $table->integer('category');
            $table->boolean('repeat');
            $table->string('sound');
            $table->boolean('enabled');
            $table->integer('fadein');
            $table->timestamps();
		});
	}

	/**
	 * Reverse the migrations.
	 *
	 * @return void
	 */
	public function down()
	{
		Schema::drop('pings');
	}

}
