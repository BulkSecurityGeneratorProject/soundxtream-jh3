(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('TrackSearch', TrackSearch);

    TrackSearch.$inject = ['$resource'];

    function TrackSearch($resource) {
        var resourceUrl =  'api/_search/tracks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
