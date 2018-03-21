@extends('app')

@section('content')

[{
    "userid": 1,
    "username": "jo_duplessis",
    "userstatus": ""
}]

<br/><br/>

    <form method="POST" action="{{ URL::to('/')  }}/login/submit">

        <input type="text" name="username" value="Default username"/>
        <input type="password" name="password" value="Default password"/>
        <input type="hidden" name="_token" value="{{{ csrf_token() }}}" />
        <input type="submit" value="Go" src="{{ URL::asset('assets/css/bootstrap.css') }}" />

    </form>

@endsection