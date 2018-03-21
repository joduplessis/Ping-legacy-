@extends('app')

@section('content')

    @foreach($test_data as $data)
        {{ $data }} <br/>
    @endforeach

@endsection

@section('footer')

    ------------------------

@endsection