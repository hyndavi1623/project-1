package com.example.instagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class InstagramApplication {

    private final JdbcTemplate jdbcTemplate;

    public InstagramApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(InstagramApplication.class, args);
    }

    // FRONTEND + BACKEND IN SAME FILE

    @GetMapping("/")
    public String home() {

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Instagram Clone</title>

            <style>

            body{
                font-family:Arial;
                background:#fafafa;
                margin:0;
            }

            .header{
                background:white;
                padding:15px;
                border-bottom:1px solid #ddd;
                text-align:center;
                font-size:24px;
                font-weight:bold;
            }

            .container{
                width:600px;
                margin:auto;
            }

            .card{
                background:white;
                margin:20px 0;
                border:1px solid #ddd;
                border-radius:10px;
                overflow:hidden;
            }

            .username{
                padding:10px;
                font-weight:bold;
            }

            img{
                width:100%;
            }

            .caption{
                padding:10px;
            }

            .form{
                background:white;
                padding:15px;
                margin-top:20px;
                border-radius:10px;
            }

            input{
                width:100%;
                padding:10px;
                margin:5px 0;
            }

            button{
                width:100%;
                padding:10px;
                background:#0095f6;
                color:white;
                border:none;
                cursor:pointer;
            }

            </style>

        </head>

        <body>

            <div class="header">
                Instagram Clone
            </div>

            <div class="container">

                <div class="form">

                    <input id="username"
                        placeholder="Username">

                    <input id="image"
                        placeholder="Image URL">

                    <input id="caption"
                        placeholder="Caption">

                    <button onclick="createPost()">
                        Create Post
                    </button>

                </div>

                <div id="feed"></div>

            </div>

            <script>

                async function loadPosts(){

                    let response =
                        await fetch('/api/posts');

                    let posts =
                        await response.json();

                    let html='';

                    posts.forEach(p=>{

                        html += `
                        <div class="card">

                            <div class="username">
                                ${p.username}
                            </div>

                            <img src="${p.image_url}">

                            <div class="caption">
                                ${p.caption}
                            </div>

                        </div>`;
                    });

                    document.getElementById('feed')
                        .innerHTML = html;
                }

                async function createPost(){

                    let data = {

                        username:
                            document.getElementById('username').value,

                        imageUrl:
                            document.getElementById('image').value,

                        caption:
                            document.getElementById('caption').value
                    };

                    await fetch('/api/posts',{
                        method:'POST',
                        headers:{
                            'Content-Type':
                            'application/json'
                        },
                        body:JSON.stringify(data)
                    });

                    loadPosts();
                }

                loadPosts();

            </script>

        </body>
        </html>
        """;
    }

    @GetMapping("/api/posts")
    public List<Map<String,Object>> posts(){

        return jdbcTemplate.queryForList("""

            SELECT
                p.id,
                u.username,
                p.image_url,
                p.caption

            FROM posts p
            JOIN users u
            ON p.user_id=u.id

            ORDER BY p.id DESC

        """);
    }

    @PostMapping("/api/posts")
    public String createPost(
            @RequestBody PostRequest request){

        Long userId;

        List<Long> users =
            jdbcTemplate.query(
                "SELECT id FROM users WHERE username=?",
                (rs,rowNum)->rs.getLong("id"),
                request.username
            );

        if(users.isEmpty()){

            jdbcTemplate.update("""

                INSERT INTO users
                (username,email,password)

                VALUES(?,?,?)

            """,
            request.username,
            request.username+"@gmail.com",
            "12345");

            userId =
                jdbcTemplate.queryForObject(
                    "SELECT id FROM users WHERE username=?",
                    Long.class,
                    request.username
                );
        }
        else{
            userId = users.get(0);
        }

        jdbcTemplate.update("""

            INSERT INTO posts
            (user_id,image_url,caption)

            VALUES(?,?,?)

        """,
        userId,
        request.imageUrl,
        request.caption);

        return "Post Created";
    }

    static class PostRequest {

        public String username;
        public String imageUrl;
        public String caption;
    }
}
