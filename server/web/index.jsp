<%
    String forwardedFor = request.getHeader("x-forwarded-proto");
    if(forwardedFor != null && !forwardedFor.equals("https")) {
        response.setHeader("Location", "https://" + request.getHeader("host"));
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link href="/assets/lib/bootstrap/css/bootstrap.css" rel="stylesheet" />
    <link href="/assets/lib/css/font-awesome.css" rel="stylesheet">
    <link href="/assets/css/styles.css" rel="stylesheet">
    <script src="/assets/lib/js/jquery-2.2.3.js"></script>
    <script src="/assets/lib/bootstrap/js/bootstrap.js"></script>
    <script>
        $(function () {
            $('.form-signin input[type=submit]').click(function () {
                var email = $('#email').val();
                var password = $('#password').val();
                $.ajax('/api/login', {
                    method: 'post',
                    data : {
                        email : email,
                        password: password
                    },
                    success : function (response) {
                        if(response.success) {
                            window.location.href = "/admin.html"
                        } else {
                            $('.error').show();
                        }
                    }
                });
            });
        });
    </script>
</head>
<body class="login">
    <div class="container">
        <div class="form-signin">
            <input type="email" id="email" class="form-control" placeholder="Email address" required autofocus>
            <input type="password" id="password" class="form-control" placeholder="Password" required>
            <div class="error alert alert-danger" style="display: none;" role="alert">
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                <span class="sr-only">Error:</span>
                Invalid username or password
            </div>
            <input class="btn btn-lg btn-primary btn-block" type="submit" value="Sign in" />
        </div>
    </div>
</body>
</html>