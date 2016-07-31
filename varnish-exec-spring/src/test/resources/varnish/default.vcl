backend localhost {
    .host = "127.0.0.1";
    .port = "@local.port@";
}

sub vcl_recv {
    set req.backend = localhost;
    return(pass);
}
