(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('PlaylistSearch', PlaylistSearch);

    PlaylistSearch.$inject = ['$resource'];

    function PlaylistSearch($resource) {
        var resourceUrl =  'api/_search/playlists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
