@extends('app')

@section('content')

    @if (count($pings))
        [
        @foreach ($pings as $ping)
            {
                "id": {{ $ping['id'] }},
                "userid": {{ $ping['name'] }},
                "title": "This is the ping title",
                "time": "00:30:45",
                "days": ["Tuesday", "Friday"]
                "category": 2,
                "repeats": true,
                "sound": "default.ogg",
                "enabled": true,
                "fadein": 5
            }
            @if ($ping['id']!=$pings->last()['id'])
                ,
            @endif
        @endforeach
        ]
    @else
        [{
            "id": -1
        }]
    @endif

    @section('footer')

        This is the footer section

    @stop

@stop