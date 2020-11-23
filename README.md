# PostsApp
Simple android app that fetch photo posts from jsonplaceholder api service with support to add, delete and update posts
the app used the jetpack components.

Jetpack components:

1- Navigation: Build and structure in-app UI and navigate between screens.
2- Data Binding: Bind UI components in layouts to data sources in app using a declarative format.
3- Paging: 	Load data in pages, and present it in a RecyclerView.
4- Room: Create, store, and manage persistent data backed by a SQLite database.

Notes:
1- The jsonplaceholder does not support data modification (add, delete or update) on the server but it will fake the response as if it really happend.
for this reason, if the response of data modification gets OK, the local database will be updated, 
but the next fetch from the server will delete any modification that happend because these modifications not actually applied on the server.

2- Due to large number of posts and to test the app features easily, I set the start page index to 162 which start to fetch the last 5 pages with page size = 30
