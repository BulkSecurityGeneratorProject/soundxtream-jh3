(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('ChatSearch', ChatSearch);

    ChatSearch.$inject = ['$resource'];

    function ChatSearch($resource) {
        var resourceUrl =  'api/_search/chats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
